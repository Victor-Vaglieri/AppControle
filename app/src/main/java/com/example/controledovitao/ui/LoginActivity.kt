package com.example.controledovitao.ui

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.example.controledovitao.databinding.LoginBinding
import com.example.controledovitao.viewmodel.LoginViewModel
import com.example.controledovitao.data.repository.AuthRepository
import androidx.lifecycle.lifecycleScope
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat
import androidx.lifecycle.asLiveData
import com.example.controledovitao.BuildConfig
import com.example.controledovitao.data.repository.ConfigRepository
import kotlinx.coroutines.launch

class LoginActivity : AppCompatActivity() {
    private lateinit var binding: LoginBinding
    private lateinit var viewModel: LoginViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = LoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel = ViewModelProvider(this)[LoginViewModel::class.java]

        if (AuthRepository.isLogged()) {
            abrirHome()
            return
        }


        lifecycleScope.launch {
            val isBiometricEnabled = ConfigRepository(application).getBiometricEnabled()
            if (isBiometricEnabled) {
                tentarLoginBiometrico()
            }
        }

        setupObservers()
        setupListeners()
    }


    private fun setupListeners() {
        binding.btnLogin.setOnClickListener {
            val login = binding.etLogin.text.toString().trim()
            val password = binding.etPassword.text.toString().trim()

            viewModel.doLogin(login, password)
        }

        binding.tvFingerprint.setOnClickListener {
            tentarLoginBiometrico()
        }
    }

    private fun setupObservers() {
        viewModel.loginSuccess.observe(this) { success ->
            if (success) {
                abrirHome()
            }
        }

        viewModel.errorMessage.observe(this) { message ->
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
        }

        viewModel.isLoading.observe(this) { carregando ->
            binding.btnLogin.isEnabled = !carregando
            binding.btnLogin.text = if (carregando) "Entrando..." else "Entrar"
        }
    }

    private fun tentarLoginBiometrico() {
        val biometricManager = BiometricManager.from(this)

        when (biometricManager.canAuthenticate(BiometricManager.Authenticators.BIOMETRIC_STRONG)) {
            BiometricManager.BIOMETRIC_SUCCESS -> mostrarDialogoBiometria()
            BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE -> Toast.makeText(this, "Sem sensor de biometria", Toast.LENGTH_SHORT).show()
            BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED -> Toast.makeText(this, "Nenhuma digital cadastrada", Toast.LENGTH_LONG).show()
            else -> Toast.makeText(this, "Biometria indisponível", Toast.LENGTH_SHORT).show()
        }
    }

    private fun mostrarDialogoBiometria() {
        val executor = ContextCompat.getMainExecutor(this)

        val callback = object : BiometricPrompt.AuthenticationCallback() {
            override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                super.onAuthenticationSucceeded(result)

                Toast.makeText(applicationContext, "Digital reconhecida! Conectando...", Toast.LENGTH_SHORT).show()

                val emailSalvo = BuildConfig.USER_EMAIL
                val senhaSalva = BuildConfig.USER_PASSWORD

                viewModel.doLogin(emailSalvo, senhaSalva)
            }

            override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                super.onAuthenticationError(errorCode, errString)
                Toast.makeText(applicationContext, "Erro: $errString", Toast.LENGTH_SHORT).show()
            }
        }

        val biometricPrompt = BiometricPrompt(this, executor, callback)
        val promptInfo = BiometricPrompt.PromptInfo.Builder()
            .setTitle("Login no Controle do Vitão")
            .setSubtitle("Use sua digital para entrar")
            .setNegativeButtonText("Usar senha")
            .build()

        biometricPrompt.authenticate(promptInfo)
    }

    private fun abrirHome() {
        val intent = Intent(this, HomeActivity::class.java)
        startActivity(intent)
        finish()
    }
}
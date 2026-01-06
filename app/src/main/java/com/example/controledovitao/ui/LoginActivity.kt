package com.example.controledovitao.ui

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.example.controledovitao.databinding.LoginBinding
import com.example.controledovitao.viewmodel.LoginViewModel


// UI/LoginActivity.kt -> viewmodel/LoginViewModel.kt -> data/repository/AuthRepository.kt
class LoginActivity : AppCompatActivity() {
    private lateinit var binding: LoginBinding
    private lateinit var viewModel: LoginViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = LoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel = ViewModelProvider(this)[LoginViewModel::class.java]

        setupObservers()

        setupListeners()

    }

    private fun setupListeners() {
        binding.btnLogin.setOnClickListener {
            val login = binding.etLogin.text.toString().trim()
            val password = binding.etPassword.text.toString().trim()

            viewModel.doLogin(login, password)
        }

        // TODO colocar acesso via digital
        binding.tvFingerprint.setOnClickListener {
            Toast.makeText(this, "Funcionalidade de Digital em breve!", Toast.LENGTH_SHORT).show()
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

            if (message.contains("Preencha")) {
                binding.etLogin.requestFocus()
            }
        }
    }

    private fun abrirHome() {
        val intent = Intent(this, HomeActivity::class.java)
        startActivity(intent)
        finish()
    }
}
package com.example.controledovitao.ui

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.example.controledovitao.R
import com.example.controledovitao.databinding.ConfigBinding
import com.example.controledovitao.viewmodel.ConfigViewModel

class ConfigActivity : AppCompatActivity() {
    private lateinit var binding: ConfigBinding
    private lateinit var viewModel: ConfigViewModel
    private var isProgrammaticChange = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ConfigBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel = ViewModelProvider(this)[ConfigViewModel::class.java]

        TopBarHelper.setupTopBar(this, binding.topBar)

        setupObservers()
        setupListeners()
    }

    private fun setupObservers() {
        viewModel.userName.observe(this) { name ->
            binding.tvUserName.text = name
        }

        viewModel.userPhoto.observe(this) { uri ->
            if (uri != null) {
                Glide.with(this).load(uri).circleCrop().into(binding.userIcon)
                binding.userIcon.setImageURI(uri)
            }
        }

        viewModel.isThemeDark.observe(this) { isDark ->
            updateSwitchSilently(binding.switchTheme, isDark)
            val mode = if (isDark) AppCompatDelegate.MODE_NIGHT_YES else AppCompatDelegate.MODE_NIGHT_NO
            AppCompatDelegate.setDefaultNightMode(mode)
        }


        viewModel.isBiometricEnabled.observe(this) { isEnabled ->
            updateSwitchSilently(binding.switchBio, isEnabled)
        }

        viewModel.isDataCollectionEnabled.observe(this) { isEnabled ->
            updateSwitchSilently(binding.switchColeta, isEnabled)
        }
    }

    private fun setupListeners() {
        binding.switchTheme.setOnCheckedChangeListener { _, isChecked ->
            if (isProgrammaticChange) return@setOnCheckedChangeListener
            viewModel.toggleTheme(isChecked)
        }

        binding.switchBio.setOnCheckedChangeListener { _, isChecked ->
            if (isProgrammaticChange) return@setOnCheckedChangeListener
            viewModel.toggleBiometric(isChecked)
        }

        binding.switchColeta.setOnCheckedChangeListener { _, isChecked ->
            if (isProgrammaticChange) return@setOnCheckedChangeListener
            viewModel.toggleDataCollection(isChecked)
        }

        binding.btnNotifications.setOnClickListener {
            val intent = Intent(this, NotificationsActivity::class.java)
            startActivity(intent)
        }

        binding.tvUserName.setOnClickListener {
            abrirDialogEditarNome()
        }
    }


    private fun updateSwitchSilently(switch: android.widget.CompoundButton, isChecked: Boolean) {
        if (switch.isChecked != isChecked) {
            isProgrammaticChange = true
            switch.isChecked = isChecked
            isProgrammaticChange = false
        }
    }

    private fun abrirDialogEditarNome() {
        val input = EditText(this)
        input.hint = "Novo nome"

        input.setText(binding.tvUserName.text)

        AlertDialog.Builder(this)
            .setTitle("Editar Nome")
            .setView(input)
            .setPositiveButton("Salvar") { _, _ ->
                val novoNome = input.text.toString()
                if (novoNome.isNotEmpty()) {
                    // TODO: Chamar viewModel.updateName(novoNome) se você criar essa função
                    Toast.makeText(this, "Nome salvo localmente (implementar no VM)", Toast.LENGTH_SHORT).show()
                    binding.tvUserName.text = novoNome
                }
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }
}
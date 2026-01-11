package com.example.controledovitao.ui

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.lifecycle.ViewModelProvider
import com.example.controledovitao.databinding.ConfigBinding
import com.example.controledovitao.viewmodel.ConfigViewModel

class ConfigActivity : AppCompatActivity() {
    private lateinit var binding: ConfigBinding
    private lateinit var viewModel: ConfigViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ConfigBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel = ViewModelProvider(this)[ConfigViewModel::class.java]

        TopBarHelper.setupTopBar(this, binding.topBar)

        setupUser()
        setupListener()
    }

    private fun setupUser() {
        val image = viewModel.image
        val userName = viewModel.userName

        if (image != null) {
            binding.userIcon.imageTintList = null
            binding.userIcon.scaleType = android.widget.ImageView.ScaleType.CENTER_CROP
            binding.userIcon.setImageURI(image)
        }
        binding.tvUserName.text = userName
    }

    private fun setupListener() {
        binding.switchTheme.setOnCheckedChangeListener { _, isChecked ->
            viewModel.updateTheme(isChecked)

            if (isChecked) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            } else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            }
        }

        binding.switchBackup.setOnCheckedChangeListener { _, isChecked ->
            viewModel.updateBackup(isChecked)
            if (isChecked) Toast.makeText(this, "Backup ativado", Toast.LENGTH_SHORT).show()
        }

        binding.switchBio.setOnCheckedChangeListener { _, isChecked ->
            viewModel.updateBiometria(isChecked)
        }

        // TODO talvez

        binding.switchColeta.setOnCheckedChangeListener { _, isChecked ->
            viewModel.updateColeta(isChecked)
        }

        binding.btnNotifications.setOnClickListener {
            val intent = Intent(this, NotificationsActivity::class.java)
            startActivity(intent)
        }

        binding.cardProfile.setOnClickListener {
            Toast.makeText(this, "Abrir Galeria", Toast.LENGTH_SHORT).show()
        }


        binding.tvUserName.setOnClickListener {
            abrirDialogEditarNome()
        }
    }

    private fun abrirDialogEditarNome() {
        // TODO: Criar um AlertDialog com um EditText dentro para mudar o nome
        Toast.makeText(this, "Editar nome do usuário", Toast.LENGTH_SHORT).show()
    }
}
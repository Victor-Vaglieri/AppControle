package com.example.controledovitao.ui

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.controledovitao.databinding.LoginBinding

class LoginActivity : AppCompatActivity() {

    // 1. Criar a variável do Binding (A "ponte" para o XML)
    // O nome "ActivityLoginBinding" é gerado automático baseado no nome do XML (activity_login.xml)
    private lateinit var binding: LoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = LoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnLogin.setOnClickListener {
            realizarLogin()
        }

        binding.tvFingerprint.setOnClickListener {
            Toast.makeText(this, "Funcionalidade de Digital em breve!", Toast.LENGTH_SHORT).show()
        }
    }

    private fun realizarLogin() {
        val login = binding.etLogin.text.toString().trim()
        val password = binding.etPassword.text.toString().trim()

        if (login.isEmpty()) {
            binding.etLogin.error = "Preencha o login"
            binding.etLogin.requestFocus()
            return
        }

        if (password.isEmpty()) {
            binding.etPassword.error = "Preencha a senha"
            binding.etPassword.requestFocus()
            return
        }

        if (login == "admin" && password == "1234") {
            abrirHome()
        } else {
            Toast.makeText(this, "Login ou senha incorretos", Toast.LENGTH_SHORT).show()
        }
    }

    private fun abrirHome() {
        val intent = Intent(this, HomeActivity::class.java)
        startActivity(intent)
        finish()
    }
}
package com.example.controledovitao.ui

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.controledovitao.databinding.PaymentMethodHomeBinding

class PaymentsActivity : AppCompatActivity() {

    private lateinit var binding: PaymentMethodHomeBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = PaymentMethodHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        TopBarHelper.setupTopBar(this, binding.topBar)

        setupListeners()
    }

    private fun setupListeners() {
        binding.btnAdd.setOnClickListener {
            startActivity(Intent(this, PaymentCreateActivity::class.java))
        }

        binding.btnEdit.setOnClickListener {
            startActivity(Intent(this, PaymentListActivity::class.java))
        }
    }
}
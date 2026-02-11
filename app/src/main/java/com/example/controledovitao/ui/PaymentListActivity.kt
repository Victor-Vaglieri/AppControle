package com.example.controledovitao.ui

import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.controledovitao.databinding.PaymentMethodListBinding
import com.example.controledovitao.ui.adapter.PaymentAdapter
import com.example.controledovitao.viewmodel.PaymentViewModel

class PaymentListActivity : AppCompatActivity() {

    private lateinit var binding:PaymentMethodListBinding
    private val viewModel: PaymentViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = PaymentMethodListBinding.inflate(layoutInflater)
        setContentView(binding.root)

        TopBarHelper.setupTopBar(this, binding.topBar)

        setupObservers()
        setupListeners()
    }

    override fun onResume() {
        super.onResume()
        viewModel.loadMethods()
    }

    private fun setupObservers() {
        viewModel.paymentMethods.observe(this) { methods ->
            val adapter = PaymentAdapter(methods) { selectedMethod ->
                val intent = Intent(this, PaymentEditActivity::class.java)
                intent.putExtra("METHOD_NAME", selectedMethod.name)
                startActivity(intent)
            }
            binding.recyclerMethods.layoutManager = LinearLayoutManager(this)
            binding.recyclerMethods.adapter = adapter
        }
    }

    private fun setupListeners() {
        binding.btnAddMethod.setOnClickListener {
            startActivity(Intent(this, PaymentCreateActivity::class.java))
        }
    }
}
package com.example.controledovitao.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.controledovitao.databinding.InvestmentsBinding
import com.example.controledovitao.ui.adapter.InvestmentsAdapter
import com.example.controledovitao.viewmodel.InvestmentsViewModel

class InvestmentsActivity : AppCompatActivity() {

    private lateinit var binding: InvestmentsBinding
    private lateinit var viewModel: InvestmentsViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = InvestmentsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel = ViewModelProvider(this)[InvestmentsViewModel::class.java]

        TopBarHelper.setupTopBar(this, binding.topBar)

        setupList()
    }

    private fun setupList() {
        binding.recyclerInvestments.layoutManager = LinearLayoutManager(this)

        viewModel.investmentsList.observe(this) { items ->
            binding.recyclerInvestments.adapter = InvestmentsAdapter(items)
        }
    }
}
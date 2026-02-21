package com.example.controledovitao.ui

import android.content.Intent
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
        setupListeners()
    }

    private fun setupList() {
        binding.recyclerInvestments.layoutManager = LinearLayoutManager(this)

        val adapter = InvestmentsAdapter(
            onEditClick = { invest ->
                val intent = Intent(this, InvestmentEditActivity::class.java)
                intent.putExtra("INVEST_ID", invest.id)
                intent.putExtra("INVEST_NAME", invest.name)
                intent.putExtra("INVEST_VALUE", invest.value)
                intent.putExtra("INVEST_PERIOD", invest.period)
                startActivity(intent)
            },
            onWithdrawClick = { invest ->
                val intent = Intent(this, InvestmentWithdrawActivity::class.java)
                intent.putExtra("INVEST_ID", invest.id)
                intent.putExtra("INVEST_NAME", invest.name)
                intent.putExtra("INVEST_VALUE", invest.value)
                startActivity(intent)
            }
        )
        binding.recyclerInvestments.adapter = adapter

        viewModel.investmentsList.observe(this) { lista ->
            adapter.updateList(lista)
        }
    }

    private fun setupListeners() {
        binding.btnBack.setOnClickListener {
            finish()
        }
    }
}
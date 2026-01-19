package com.example.controledovitao.ui

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.controledovitao.databinding.ReportsBinding
import com.example.controledovitao.ui.adapter.ChartAdapter
import com.example.controledovitao.viewmodel.ReportsViewModel

class ReportsActivity : AppCompatActivity() {

    private lateinit var binding: ReportsBinding
    private lateinit var viewModel: ReportsViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ReportsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel = ViewModelProvider(this)[ReportsViewModel::class.java]

        TopBarHelper.setupTopBar(this, binding.topBar)

        setupCharts()
        setupOptions()
        setupExportButtons()
    }

    private fun setupCharts() {
        val adapter = ChartAdapter(viewModel.mockCharts)
        binding.recyclerCharts.layoutManager = LinearLayoutManager(this)
        binding.recyclerCharts.adapter = adapter
    }

    private fun setupOptions() {
        viewModel.limitAlert.observe(this) { value ->
            binding.txtLimitValue.text = "$value%"
        }
        binding.btnLimitPlus.setOnClickListener { viewModel.changeLimit(5) }
        binding.btnLimitMinus.setOnClickListener { viewModel.changeLimit(-5) }

        viewModel.daysCount.observe(this) { value ->
            binding.txtDaysValue.text = value.toString()
        }
        binding.btnDaysPlus.setOnClickListener { viewModel.changeDays(1) }
        binding.btnDaysMinus.setOnClickListener { viewModel.changeDays(-1) }
    }

    private fun setupExportButtons() {
        binding.btnExportExcel.setOnClickListener {
            Toast.makeText(this, "Gerando arquivo Excel...", Toast.LENGTH_SHORT).show()
        }

        binding.btnExportPDF.setOnClickListener {
            Toast.makeText(this, "Gerando arquivo PDF...", Toast.LENGTH_SHORT).show()
        }
    }
}
package com.example.controledovitao.ui

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.example.controledovitao.databinding.HomeBinding
import com.example.controledovitao.viewmodel.HomeViewModel
import java.math.BigDecimal
import java.util.Objects.toString

class HomeActivity : AppCompatActivity() {
    private lateinit var binding: HomeBinding

    private lateinit var viewModel: HomeViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = HomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel = ViewModelProvider(this)[HomeViewModel::class.java]

        TopBarHelper.setupTopBar(this, binding)

        setupBalance()
        setupChips()
        setupOptions()

    }

    private fun setupBalance() {
        // TODO mudar para viewModel
        var invest:BigDecimal  = BigDecimal("0")
        var balance:BigDecimal = BigDecimal("0")
        var valueProgress:Int = 0
        var limit: BigDecimal = BigDecimal("0")
        var usage: BigDecimal = limit * BigDecimal(toString(valueProgress))
        var rest: BigDecimal = limit - usage

        binding.valInvestido.setText(toString(invest))
        binding.valSaldo.setText(toString(balance))
        binding.progressBarLimit.setProgress(valueProgress)
        binding.txtLimitPercentage.setText("${valueProgress}%")
        binding.txtLimitValues.setText("${usage} / ${rest}")
    }

    private fun setupChips() {
    // TODO ja fazer com viewModel
    }

    private fun setupOptions() {

        // TODO fazer as telas
        binding.optAddExpense.setOnClickListener {
            Toast.makeText(this, "Clicou em Adicionar Gasto", Toast.LENGTH_SHORT).show()
            // Futuramente: startActivity(Intent(this, AddExpenseActivity::class.java))
        }

        binding.optSimulator.setOnClickListener {
            Toast.makeText(this, "Clicou em Simulador", Toast.LENGTH_SHORT).show()
        }

        binding.optInvestments.setOnClickListener {
            Toast.makeText(this, "Clicou em Investimentos", Toast.LENGTH_SHORT).show()
        }


        binding.expense1.setOnClickListener {
            Toast.makeText(this, "Detalhes do Gasto X", Toast.LENGTH_SHORT).show()
        }
    }


}

package com.example.controledovitao.ui

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.example.controledovitao.databinding.HomeBinding
import com.example.controledovitao.viewmodel.HomeViewModel
import java.math.BigDecimal
import com.example.controledovitao.ui.components.ExpenseItemView

import java.math.RoundingMode
import java.text.NumberFormat
import java.util.Locale

class HomeActivity : AppCompatActivity() {
    private lateinit var  binding: HomeBinding

    private lateinit var viewModel: HomeViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = HomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel = ViewModelProvider(this)[HomeViewModel::class.java]

        TopBarHelper.setupTopBar(this, binding)
        setupBalance()
        setupOptions()
        setupChips()
    }

    private fun correctString(number: BigDecimal, option: Boolean): String {
        // TODO pegar coin de uma config
        val coin = "R$"
        val localeBR = Locale.of("pt", "BR")
        val formatator = NumberFormat.getNumberInstance(localeBR)
        val transform = formatator.format(number)
        if (option){
            return coin + " " + transform
        } else {
            return transform
        }
    }

    private fun setupBalance() {
        val invest: BigDecimal = viewModel.invest
        val balance: BigDecimal = viewModel.balance
        val limit: BigDecimal = viewModel.limit
        val usage: BigDecimal = viewModel.usage

        val valueProgress = if (limit.compareTo(BigDecimal.ZERO) == 0) {
            0
        } else {
                usage.divide(limit, 2, RoundingMode.HALF_UP)
                .multiply(BigDecimal("100"))
                .toInt()
        }

        val rest: BigDecimal = limit.subtract(usage)

        binding.valInvestido.text = correctString(invest, true)
        binding.valSaldo.text = correctString(balance, true)
        binding.progressBarLimit.progress = valueProgress
        binding.txtLimitPercentage.text = "$valueProgress%"
        binding.txtLimitValues.text =
            "${correctString(usage, false)} / ${correctString(limit, false)}"
    }

    private fun setupOptions() {

        // TODO fazer as telas, por enquanto deixar assim
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


    }

    private fun setupChips() {
        viewModel.spentItems.observe(this) { items ->

            binding.containerRecentExpenses.removeAllViews()

            items.forEachIndexed { index, (title, subtitle) ->

                val view = ExpenseItemView(this).apply {
                    setExpenseTitle(title)
                    setExpenseSubtitle(subtitle)
                }

                binding.containerRecentExpenses.addView(view)
            }
        }
    }


}

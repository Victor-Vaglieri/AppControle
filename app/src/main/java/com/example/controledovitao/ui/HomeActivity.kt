package com.example.controledovitao.ui

import android.content.Intent
import android.os.Bundle
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

        TopBarHelper.setupTopBar(this, binding.topBar)
        setupBalance()
        setupOptionsAndMethods()
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

    private fun setupOptionsAndMethods() {

        binding.optAddExpense.setOnClickListener {
            Toast.makeText(this, "Clicou em Adicionar Gasto", Toast.LENGTH_SHORT).show()
            openAddSpent()
        }

        binding.optSimulator.setOnClickListener {
            Toast.makeText(this, "Clicou em Simulador", Toast.LENGTH_SHORT).show()
            openSimulatorInvest()
        }

        binding.optInvestments.setOnClickListener {
            Toast.makeText(this, "Clicou em Investimentos", Toast.LENGTH_SHORT).show()
            openInvest()
        }

        binding.optPayment.setOnClickListener {
            Toast.makeText(this, "Clicou em Pagamentos", Toast.LENGTH_SHORT).show()
            openPayment()
        }

        binding.optReports.setOnClickListener {
            Toast.makeText(this, "Clicou em Relatórios", Toast.LENGTH_SHORT).show()
            openReports()
        }

        // TODO adicionar os metodos de pagamentos "methods" na estrutura "scrollChips", porem as paginas serão por navegação dinamica

    }

    private fun openAddSpent() {
        val intent = Intent(this, SpentCreateActivity::class.java)
        startActivity(intent)
        setupBalance()
        setupChips()
    }
    private fun openSimulatorInvest() {
        val intent = Intent(this, SimulatorInvestActivity::class.java)
        startActivity(intent)
        setupBalance()
        setupChips()
    }

    private fun openInvest() {
        val intent = Intent(this, investsActivity::class.java)
        startActivity(intent)
        setupBalance()
        setupChips()
    }

    private fun openPayment() {
        val intent = Intent(this, paymentsActivity::class.java)
        startActivity(intent)
        setupBalance()
        setupChips()
    }

    private fun openReports() {
        val intent = Intent(this, reportsActivity::class.java)
        startActivity(intent)
        setupBalance()
        setupChips()
    }


    private fun setupChips() {
        // TODO por listener em todos (AFFFFF)
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

package com.example.controledovitao.ui

import com.example.controledovitao.R
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
        setupMethods()
        setupBalance()
        setupOptionsAndMethods()
        setupChips()
    }

    private fun correctString(number: BigDecimal, option: Boolean): String {
        val localeBR = Locale.of("pt", "BR")
        val formatator = NumberFormat.getNumberInstance(localeBR)

        formatator.minimumFractionDigits = 2
        formatator.maximumFractionDigits = 2

        val transform = formatator.format(number)

        // TODO: idealmente pegar "R$" de uma config ou currency.symbol
        val coin = "R$"

        return if (option) {
            "$coin $transform"
        } else {
            transform
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


        binding.valInvestido.text = correctString(invest, true)
        binding.valSaldo.text = correctString(balance, true)
        binding.progressBarLimit.progress = valueProgress
        binding.txtLimitPercentage.text = "$valueProgress%"
        binding.txtLimitValues.text =
            "${correctString(usage, false)} / ${correctString(limit, false)}"
    }



    private var selectedFilter: String = "TODOS"
    private fun setupMethods() {
        val container = binding.containerFilter
        container.removeAllViews()

        val listaNomes = mutableListOf("TODOS")
        listaNomes.addAll(viewModel.methods)

        listaNomes.forEach { nomeMetodo ->

            val chip = android.widget.TextView(this)

            chip.text = nomeMetodo.uppercase()
            chip.textSize = 12f
            chip.typeface = android.graphics.Typeface.DEFAULT_BOLD

            val padH = dpToPx(20)
            val padV = dpToPx(8)
            chip.setPadding(padH, padV, padH, padV)

            val params = android.widget.LinearLayout.LayoutParams(
                android.widget.LinearLayout.LayoutParams.WRAP_CONTENT,
                android.widget.LinearLayout.LayoutParams.WRAP_CONTENT
            )
            params.marginEnd = dpToPx(8)
            chip.layoutParams = params

            // Listener AQUI
            chip.setOnClickListener {
                selectedFilter = nomeMetodo
                atVisualMethos()

                // Trocar para pagina de review de cada metodo
                // if (todos) home
                Toast.makeText(this, "Filtro: $nomeMetodo", Toast.LENGTH_SHORT).show()
            }
            container.addView(chip)
        }
        atVisualMethos()
    }

    private fun atVisualMethos() {
        val container = binding.containerFilter

        for (i in 0 until container.childCount) {
            val child = container.getChildAt(i) as android.widget.TextView
            val textoChip = child.text.toString()

            if (textoChip.equals(selectedFilter, ignoreCase = true)) {
                child.setBackgroundResource(R.drawable.bg_filter_active)
                child.setTextColor(getColor(R.color.neutral_light_lightest))
            } else {
                child.setBackgroundResource(R.drawable.bg_filter_inactive)
                child.setTextColor(getColor(R.color.highlight_darkest))
            }
        }
    }

    private fun dpToPx(dp: Int): Int {
        return (dp * resources.displayMetrics.density).toInt()
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
        val intent = Intent(this, InvestsActivity::class.java)
        startActivity(intent)
        setupBalance()
        setupChips()
    }

    private fun openPayment() {
        val intent = Intent(this, PaymentsActivity::class.java)
        startActivity(intent)
        setupBalance()
        setupChips()
    }

    private fun openReports() {
        val intent = Intent(this, ReportsActivity::class.java)
        startActivity(intent)
        setupBalance()
        setupChips()
    }


    private fun setupChips() {
        viewModel.spentItems.observe(this) { items ->

            binding.containerRecentExpenses.removeAllViews()

            items.forEachIndexed { index, (title,subtitle,item) ->

                val view = ExpenseItemView(this).apply {
                    setExpenseTitle(title)
                    setExpenseSubtitle(subtitle)
                }
                view.setOnClickListener {
                    val intent = Intent(this, SpentViewActivity::class.java)

                    intent.putExtra("EXTRA_SPENT", item)

                    startActivity(intent)
                }

                binding.containerRecentExpenses.addView(view)
            }
        }
    }


}

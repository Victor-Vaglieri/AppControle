package com.example.controledovitao.ui

import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.example.controledovitao.R
import com.example.controledovitao.databinding.HomeBinding
import com.example.controledovitao.ui.components.ExpenseItemView
import com.example.controledovitao.viewmodel.HomeViewModel
import java.math.BigDecimal
import java.math.RoundingMode
import java.text.NumberFormat
import java.util.Locale

class HomeActivity : AppCompatActivity() {
    private lateinit var binding: HomeBinding
    private lateinit var viewModel: HomeViewModel
    private var selectedFilter: String = "TODOS"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = HomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel = ViewModelProvider(this)[HomeViewModel::class.java]

        TopBarHelper.setupTopBar(this, binding.topBar)

        setupObservers()
        setupOptionsAndMethods()
    }

    private fun setupObservers() {
        viewModel.totalBalance.observe(this) { updateBalanceDisplay() }
        viewModel.totalLimit.observe(this) { updateBalanceDisplay() }
        viewModel.totalUsage.observe(this) { updateBalanceDisplay() }
        viewModel.totalInvest.observe(this) { updateBalanceDisplay() }
        viewModel.bestCardName.observe(this) { name ->
            binding.tvBestCardName.text = name
        }
        viewModel.methodNames.observe(this) { methods ->
            setupChips(methods)
        }
        viewModel.recentSpents.observe(this) { items ->
            binding.containerRecentExpenses.removeAllViews()


            items.forEach { (title, values, item) ->
                val view = ExpenseItemView(this).apply {
                    setExpenseTitle(title)
                    setExpenseSubtitle(toSubtitle(values.first, values.second))
                }
                view.setOnClickListener {
                    val intent = Intent(this@HomeActivity, SpentViewActivity::class.java)
                    intent.putExtra("EXTRA_SPENT", item)
                    startActivity(intent)
                }
                binding.containerRecentExpenses.addView(view)
            }
        }
        viewModel.errorMessage.observe(this) { msg ->
            Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
        }
    }
    private fun updateBalanceDisplay() {
        val invest = viewModel.totalInvest.value ?: BigDecimal.ZERO
        val balance = viewModel.totalBalance.value ?: BigDecimal.ZERO
        val limit = viewModel.totalLimit.value ?: BigDecimal.ZERO
        val usage = viewModel.totalUsage.value ?: BigDecimal.ZERO

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
    private fun setupChips(methodNames: List<String>) {
        val container = binding.containerFilter
        container.removeAllViews()

        val listaNomes = mutableListOf("TODOS")
        listaNomes.addAll(methodNames)

        listaNomes.forEach { nomeMetodo ->
            val chip = TextView(this)
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

            chip.setOnClickListener {
                selectedFilter = nomeMetodo
                atVisualMethos()
                viewModel.filterSpents(nomeMetodo)

                Toast.makeText(this, "Filtrando: $nomeMetodo", Toast.LENGTH_SHORT).show()
            }
            container.addView(chip)
        }
        atVisualMethos()
    }

    private fun atVisualMethos() {
        val container = binding.containerFilter
        for (i in 0 until container.childCount) {
            val child = container.getChildAt(i) as TextView
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

    private fun setupOptionsAndMethods() {
        binding.optAddExpense.setOnClickListener { openActivity(SpentCreateActivity::class.java) }
        binding.optSimulator.setOnClickListener { openActivity(SimulatorInvestActivity::class.java) }
        binding.optInvestments.setOnClickListener { openActivity(InvestmentsActivity::class.java) }
        binding.optPayment.setOnClickListener { openActivity(PaymentsActivity::class.java) }
        binding.optReports.setOnClickListener { openActivity(ReportsActivity::class.java) }
    }

    private fun openActivity(clazz: Class<*>) {
        val intent = Intent(this, clazz)
        startActivity(intent)
    }

    private fun correctString(number: BigDecimal, option: Boolean): String {
        val localeBR = Locale("pt", "BR")
        val formatator = NumberFormat.getNumberInstance(localeBR)
        formatator.minimumFractionDigits = 2
        formatator.maximumFractionDigits = 2
        val transform = formatator.format(number)
        val coin = "R$"
        return if (option) "$coin $transform" else transform
    }

    private fun toSubtitle(value: BigDecimal, times: Int): String {
        val localeBR = Locale("pt", "BR")
        val formatator = NumberFormat.getNumberInstance(localeBR)
        formatator.minimumFractionDigits = 2
        formatator.maximumFractionDigits = 2
        val transform = formatator.format(value)
        return if (times != 0) "$transform x $times" else "$transform"
    }

    private fun dpToPx(dp: Int): Int {
        return (dp * resources.displayMetrics.density).toInt()
    }
}
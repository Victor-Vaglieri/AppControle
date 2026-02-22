package com.example.controledovitao.ui

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.controledovitao.R
import com.example.controledovitao.data.model.Payment
import com.example.controledovitao.data.model.Spent
import com.example.controledovitao.databinding.HomeBinding
import com.example.controledovitao.ui.components.ExpenseItemView
import com.example.controledovitao.viewmodel.HomeViewModel
import java.math.BigDecimal
import java.math.RoundingMode
import java.text.NumberFormat
import java.util.Locale
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.formatter.ValueFormatter
import java.text.SimpleDateFormat
import java.util.Date
import android.graphics.Color

class HomeActivity : AppCompatActivity() {

    private lateinit var binding: HomeBinding
    private lateinit var viewModel: HomeViewModel
    private var selectedFilter: String = "TODOS"

    private val detailsAdapter = SimpleExpenseAdapter { spent ->
        openSpentDetails(spent)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = HomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel = ViewModelProvider(this)[HomeViewModel::class.java]
        TopBarHelper.setupTopBar(this, binding.topBar)

        setupUI()
        setupObservers()
        setupListeners()
        setupChartDesign()
    }

    private fun setupChartDesign() {
        val chart = binding.chartExpenses

        chart.description.isEnabled = false
        chart.legend.isEnabled = false
        chart.setDrawGridBackground(false)
        chart.setDrawBorders(false)

        val xAxis = chart.xAxis
        xAxis.position = XAxis.XAxisPosition.BOTTOM
        xAxis.setDrawGridLines(false)
        xAxis.textColor = Color.WHITE
        xAxis.granularity = 1f

        chart.axisLeft.isEnabled = false

        chart.axisRight.isEnabled = true
        chart.axisRight.textColor = Color.WHITE
        chart.axisRight.setDrawGridLines(true)
        chart.axisRight.gridColor = Color.parseColor("#404040")

        chart.animateY(1000)
        chart.invalidate()
    }

    private fun setupUI() {
        binding.recyclerCardSpents.layoutManager = LinearLayoutManager(this)
        binding.recyclerCardSpents.adapter = detailsAdapter
    }

    private fun updateChartData(spents: List<Spent>) {
        val chart = binding.chartExpenses

        if (spents.isEmpty()) {
            chart.clear()
            chart.setNoDataText("Sem gastos para exibir")
            chart.setNoDataTextColor(Color.WHITE)
            return
        }
        val expensesByDay = spents.groupBy {
            SimpleDateFormat("dd", Locale("pt", "BR")).format(Date(it.spentDate)).toInt()
        }.mapValues { entry ->
            entry.value.sumOf { it.value }
        }.toSortedMap()

        val entries = ArrayList<BarEntry>()
        val labels = ArrayList<String>()

        expensesByDay.forEach { (day, totalValue) ->
            entries.add(BarEntry(day.toFloat(), totalValue.toFloat()))
        }
        val dataSet = BarDataSet(entries, "Gastos")
        dataSet.color = getColor(R.color.highlight_darkest)
        dataSet.valueTextColor = Color.WHITE
        dataSet.valueTextSize = 10f

        dataSet.valueFormatter = object : ValueFormatter() {
            override fun getFormattedValue(value: Float): String {
                if (value == 0f) return ""
                return String.format("%.0f", value)
            }
        }
        val data = BarData(dataSet)
        data.barWidth = 0.6f

        chart.data = data

        chart.xAxis.valueFormatter = object : ValueFormatter() {
            override fun getFormattedValue(value: Float): String {
                return String.format("%02d", value.toInt())
            }
        }

        chart.notifyDataSetChanged()
        chart.animateY(1000)
        chart.invalidate()
    }

    private fun setupObservers() {
        viewModel.totalBalance.observe(this) { updateDashboardValues() }
        viewModel.totalLimit.observe(this) { updateDashboardValues() }
        viewModel.totalUsage.observe(this) { updateDashboardValues() }
        viewModel.totalInvest.observe(this) { updateDashboardValues() }
        viewModel.bestCardName.observe(this) { binding.tvBestCardName.text = it }

        viewModel.methodNames.observe(this) { methods ->
            setupChips(methods)
        }

        viewModel.selectedPaymentMethod.observe(this) { payment ->
            if (payment == null) {
                binding.containerDashboard.visibility = View.VISIBLE
                binding.containerCardDetails.visibility = View.GONE
            } else {
                binding.containerDashboard.visibility = View.GONE
                binding.containerCardDetails.visibility = View.VISIBLE
                updateCardDetailsUI(payment)
                updateChartData(payment.spent)
            }
        }

        viewModel.recentSpents.observe(this) { items ->
            binding.containerRecentExpenses.removeAllViews()
            items.forEach { (title, values, item) ->
                val view = ExpenseItemView(this).apply {
                    setExpenseTitle(title)
                    setExpenseSubtitle(toSubtitle(values.first, values.second))
                }
                view.setOnClickListener { openSpentDetails(item) }
                binding.containerRecentExpenses.addView(view)
            }
            val rawList = items.map { it.third }
            detailsAdapter.submitList(rawList)
        }

        viewModel.errorMessage.observe(this) {
            Toast.makeText(this, it, Toast.LENGTH_SHORT).show()
        }
    }

    private fun updateCardDetailsUI(payment: Payment) {
        val localeBR = Locale("pt", "BR")
        binding.tvCardLimit.text = String.format(localeBR, "%.2f", payment.limit ?: 0.0)
        binding.tvCardBalance.text = String.format(localeBR, "%.2f", payment.balance)
    }

    private fun setupChips(methodNames: List<String>) {
        val container = binding.containerFilter
        container.removeAllViews()

        val listaNomes = mutableListOf("TODOS")
        listaNomes.addAll(methodNames)

        listaNomes.forEach { nomeMetodo ->
            val chip = TextView(this)
            chip.text = nomeMetodo.uppercase()
            chip.textSize = 14f
            chip.typeface = android.graphics.Typeface.DEFAULT_BOLD

            val padH = dpToPx(20)
            val padV = dpToPx(8)
            chip.setPadding(padH, padV, padH, padV)

            val params = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
            params.marginEnd = dpToPx(8)
            chip.layoutParams = params

            chip.setOnClickListener {
                selectedFilter = nomeMetodo
                updateVisualChips()

                viewModel.selectMethod(nomeMetodo)
            }
            container.addView(chip)
        }
        updateVisualChips()
    }

    private fun updateVisualChips() {
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

    private fun setupListeners() {
        binding.optAddExpense.setOnClickListener { openActivity(SpentCreateActivity::class.java) }
        binding.optSimulator.setOnClickListener { openActivity(SimulatorInvestActivity::class.java) }
        binding.optInvestments.setOnClickListener { openActivity(InvestmentsActivity::class.java) }
        binding.optPayment.setOnClickListener { openActivity(PaymentsActivity::class.java) }
        binding.optReports.setOnClickListener { openActivity(ReportsActivity::class.java) }

        binding.btnLimitPlus.setOnClickListener { viewModel.updateSelectedCardLimit(50.0) }
        binding.btnLimitMinus.setOnClickListener { viewModel.updateSelectedCardLimit(-50.0) }

        binding.btnBalancePlus.setOnClickListener { viewModel.updateSelectedCardBalance(10.0) }
        binding.btnBalanceMinus.setOnClickListener { viewModel.updateSelectedCardBalance(-10.0) }

        binding.btnCloseInvoice.setOnClickListener {
            viewModel.closeInvoice()
        }
    }

    private fun updateDashboardValues() {
        val invest = viewModel.totalInvest.value ?: BigDecimal.ZERO
        val balance = viewModel.totalBalance.value ?: BigDecimal.ZERO
        val limit = viewModel.totalLimit.value ?: BigDecimal.ZERO
        val usage = viewModel.totalUsage.value ?: BigDecimal.ZERO

        val valueProgress = if (limit.compareTo(BigDecimal.ZERO) == 0) 0
        else usage.divide(limit, 2, RoundingMode.HALF_UP).multiply(BigDecimal("100")).toInt()

        binding.valInvestido.text = correctString(invest, true)
        binding.valSaldo.text = correctString(balance, true)
        binding.progressBarLimit.progress = valueProgress
        binding.txtLimitPercentage.text = "$valueProgress%"
        binding.txtLimitValues.text = "${correctString(usage, false)} / ${correctString(limit, false)}"
    }

    private fun openSpentDetails(spent: Spent) {
        val intent = Intent(this, SpentViewActivity::class.java)
        intent.putExtra("EXTRA_SPENT", spent)
        startActivity(intent)
    }

    private fun openActivity(clazz: Class<*>) {
        startActivity(Intent(this, clazz))
    }

    private fun correctString(number: BigDecimal, option: Boolean): String {
        val localeBR = Locale("pt", "BR")
        val formatator = NumberFormat.getNumberInstance(localeBR)
        formatator.minimumFractionDigits = 2
        formatator.maximumFractionDigits = 2
        val transform = formatator.format(number)
        return if (option) "R$ $transform" else transform
    }

    private fun toSubtitle(value: BigDecimal, times: Int): String {
        val localeBR = Locale("pt", "BR")
        val formatator = NumberFormat.getNumberInstance(localeBR)
        formatator.minimumFractionDigits = 2
        formatator.maximumFractionDigits = 2
        val transform = formatator.format(value)
        return if (times > 1) "$transform x $times" else transform
    }

    private fun dpToPx(dp: Int): Int {
        return (dp * resources.displayMetrics.density).toInt()
    }

    inner class SimpleExpenseAdapter(private val onClick: (Spent) -> Unit) : RecyclerView.Adapter<SimpleExpenseAdapter.ViewHolder>() {

        private var items = listOf<Spent>()

        fun submitList(newItems: List<Spent>) {
            items = newItems
            notifyDataSetChanged()
        }

        inner class ViewHolder(val view: ExpenseItemView) : RecyclerView.ViewHolder(view)

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val view = ExpenseItemView(parent.context)
            view.layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
            return ViewHolder(view)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            val item = items[position]
            holder.view.setExpenseTitle(item.name)

            val valBig = BigDecimal.valueOf(item.value)
            holder.view.setExpenseSubtitle(toSubtitle(valBig, item.times))

            holder.view.setOnClickListener { onClick(item) }
        }

        override fun getItemCount() = items.size
    }
}
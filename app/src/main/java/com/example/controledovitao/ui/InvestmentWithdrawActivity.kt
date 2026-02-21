package com.example.controledovitao.ui

import android.content.Context
import android.os.Bundle
import android.view.KeyEvent
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.controledovitao.R
import com.example.controledovitao.data.model.Payment
import com.example.controledovitao.databinding.InvestmentWithdrawBinding
import com.example.controledovitao.viewmodel.InvestmentWithdrawViewModel
import java.math.BigDecimal
import java.text.NumberFormat
import java.util.Locale

class InvestmentWithdrawActivity : AppCompatActivity() {

    private lateinit var binding: InvestmentWithdrawBinding
    private lateinit var viewModel: InvestmentWithdrawViewModel

    private val destinationAdapter = DestinationAdapter { selectedPayment ->
        viewModel.selectPayment(selectedPayment)
        binding.recyclerDestination.visibility = View.GONE
        binding.spinnerDestination.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.icon_down, 0)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = InvestmentWithdrawBinding.inflate(layoutInflater)
        setContentView(binding.root)

        TopBarHelper.setupTopBar(this, binding.topBar)
        viewModel = ViewModelProvider(this)[InvestmentWithdrawViewModel::class.java]

        val id = intent.getStringExtra("INVEST_ID") ?: ""
        val name = intent.getStringExtra("INVEST_NAME") ?: "Desconhecido"
        val valueBase = intent.getDoubleExtra("INVEST_VALUE", 0.0)
        val valueEstimated = intent.getDoubleExtra("INVEST_ESTIMATE", valueBase)

        binding.tvInvestName.text = name

        val localeBR = Locale("pt", "BR")
        val format = NumberFormat.getCurrencyInstance(localeBR)
        binding.tvBaseValue.text = format.format(valueBase)

        viewModel.initData(id, name, valueBase, valueEstimated)

        setupUI()
        setupObservers()
    }

    private fun setupUI() {
        binding.recyclerDestination.layoutManager = LinearLayoutManager(this)
        binding.recyclerDestination.adapter = destinationAdapter

        binding.spinnerDestination.setOnClickListener {
            if (binding.recyclerDestination.visibility == View.VISIBLE) {
                binding.recyclerDestination.visibility = View.GONE
                binding.spinnerDestination.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.icon_down, 0)
            } else {
                binding.recyclerDestination.visibility = View.VISIBLE
                binding.spinnerDestination.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.icon_up, 0)
            }
        }

        binding.btnWithdrawPlus.setOnClickListener { viewModel.changeWithdrawValue(BigDecimal("100")) }
        binding.btnWithdrawMinus.setOnClickListener { viewModel.changeWithdrawValue(BigDecimal("-100")) }

        binding.etWithdrawValue.setOnEditorActionListener { v, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_DONE || event?.keyCode == KeyEvent.KEYCODE_ENTER) {
                val cleanStr = v.text.toString().replace(".", "").replace(",", ".")
                cleanStr.toBigDecimalOrNull()?.let { viewModel.setExactWithdrawValue(it) }

                val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                imm.hideSoftInputFromWindow(v.windowToken, 0)
                v.clearFocus()
                true
            } else {
                false
            }
        }

        binding.btnBack.setOnClickListener { finish() }

        binding.btnConfirmWithdraw.setOnClickListener {
            val cleanStr = binding.etWithdrawValue.text.toString().replace(".", "").replace(",", ".")
            cleanStr.toBigDecimalOrNull()?.let { viewModel.setExactWithdrawValue(it) }

            viewModel.confirmWithdrawal()
        }
    }

    private fun setupObservers() {
        val localeBR = Locale("pt", "BR")
        val currencyFormat = NumberFormat.getCurrencyInstance(localeBR)
        val numberFormat = NumberFormat.getInstance(localeBR)
        numberFormat.minimumFractionDigits = 2
        numberFormat.maximumFractionDigits = 2

        viewModel.estimatedTotal.observe(this) { total ->
            binding.tvEstimatedTotal.text = currencyFormat.format(total)
        }

        viewModel.withdrawValue.observe(this) { value ->
            if (!binding.etWithdrawValue.hasFocus()) {
                binding.etWithdrawValue.setText(numberFormat.format(value))
            }
        }

        viewModel.availablePayments.observe(this) { payments ->
            destinationAdapter.submitList(payments)
        }

        viewModel.selectedPayment.observe(this) { payment ->
            binding.spinnerDestination.text = payment?.name ?: "Selecione..."
        }

        viewModel.withdrawStatus.observe(this) { success ->
            if (success) {
                Toast.makeText(this, "Resgate concluído! Valor adicionado à conta.", Toast.LENGTH_LONG).show()
                finish()
            } else {
                Toast.makeText(this, "Erro no resgate. Verifique as informações.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    inner class DestinationAdapter(private val onClick: (Payment) -> Unit) : RecyclerView.Adapter<DestinationAdapter.ViewHolder>() {
        private var items = listOf<Payment>()

        fun submitList(list: List<Payment>) {
            items = list
            notifyDataSetChanged()
        }

        inner class ViewHolder(val view: TextView) : RecyclerView.ViewHolder(view)

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val textView = TextView(parent.context).apply {
                layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
                setPadding(48, 32, 48, 32)
                textSize = 16f
                setTextColor(getColor(R.color.neutral_dark_darkest))
            }
            return ViewHolder(textView)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            val item = items[position]
            holder.view.text = item.name
            holder.view.setOnClickListener { onClick(item) }
        }

        override fun getItemCount() = items.size
    }
}
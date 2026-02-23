package com.example.controledovitao.ui

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.example.controledovitao.data.model.Payment
import com.example.controledovitao.databinding.BillBinding
import com.example.controledovitao.ui.components.ExpenseItemView
import com.example.controledovitao.viewmodel.BillViewModel
import java.text.NumberFormat
import java.util.Locale

class BillActivity : AppCompatActivity() {

    private lateinit var viewModel: BillViewModel
    private lateinit var binding: BillBinding
    private lateinit var payment: Payment

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = BillBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel = ViewModelProvider(this)[BillViewModel::class.java]

        TopBarHelper.setupTopBar(this, binding.topBar)

        val passedPayment = intent.getParcelableExtra<Payment>("EXTRA_PAYMENT")

        if (passedPayment == null) {
            Toast.makeText(this, "Erro ao carregar dados.", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        this.payment = passedPayment

        setupUI()
        setupObservers()
        setupListeners()
    }

    private fun setupObservers() {
        viewModel.closeStatus.observe(this) { success ->
            if (success) {
                Toast.makeText(this, "Fatura fechada! Gastos zerados.", Toast.LENGTH_SHORT).show()
                finish()
            } else {
                Toast.makeText(this, "Erro ao fechar fatura. Tente novamente.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun setupUI() {
        binding.tvCardName.text = payment.name

        val totalGastos = payment.spent.sumOf { it.value }
        val localeBR = Locale("pt", "BR")
        val currencyFormatter = NumberFormat.getCurrencyInstance(localeBR)
        binding.tvTotalValue.text = currencyFormatter.format(totalGastos)
        binding.containerAllExpenses.removeAllViews()

        val numberFormatter = NumberFormat.getNumberInstance(localeBR)
        numberFormatter.minimumFractionDigits = 2
        numberFormatter.maximumFractionDigits = 2
        payment.spent.forEach { spent ->
            val expenseView = ExpenseItemView(this)

            expenseView.setExpenseTitle(spent.name)

            val valorFormatado = numberFormatter.format(spent.value)
            if (spent.times > 1) {
                expenseView.setExpenseSubtitle("$valorFormatado x ${spent.times}")
            } else {
                expenseView.setExpenseSubtitle(valorFormatado)
            }

            binding.containerAllExpenses.addView(expenseView)
        }
    }

    private fun setupListeners() {
        binding.btnBack.setOnClickListener {
            finish()
        }

        binding.btnConfirm.setOnClickListener {
            viewModel.confirmInvoiceClose(payment.id)
        }
    }
}
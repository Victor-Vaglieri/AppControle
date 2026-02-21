package com.example.controledovitao.ui

import android.content.Context
import android.os.Bundle
import android.view.KeyEvent
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.example.controledovitao.databinding.InvestmentEditBinding
import com.example.controledovitao.viewmodel.InvestmentEditViewModel
import java.math.BigDecimal
import java.text.NumberFormat
import java.util.Locale

class InvestmentEditActivity : AppCompatActivity() {

    private lateinit var binding: InvestmentEditBinding
    private lateinit var viewModel: InvestmentEditViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = InvestmentEditBinding.inflate(layoutInflater)
        setContentView(binding.root)

        TopBarHelper.setupTopBar(this, binding.topBar)
        viewModel = ViewModelProvider(this)[InvestmentEditViewModel::class.java]

        // 1. Resgata os dados da Intent passados pelo Adapter
        val id = intent.getStringExtra("INVEST_ID") ?: ""
        val name = intent.getStringExtra("INVEST_NAME") ?: "Desconhecido"
        val value = intent.getDoubleExtra("INVEST_VALUE", 0.0)
        val period = intent.getStringExtra("INVEST_PERIOD") ?: "P1Y0M"

        binding.spinnerInvestment.text = name

        viewModel.initData(id, name, value, period)

        setupInputs()
        setupObservers()
    }

    private fun setupInputs() {
        binding.btnValPlus.setOnClickListener { viewModel.changeValue(BigDecimal("100")) }
        binding.btnValMinus.setOnClickListener { viewModel.changeValue(BigDecimal("-100")) }

        binding.etInvestValue.setOnEditorActionListener { v, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_DONE || event?.keyCode == KeyEvent.KEYCODE_ENTER) {
                val cleanStr = v.text.toString().replace(".", "").replace(",", ".")
                val newVal = cleanStr.toBigDecimalOrNull()

                if (newVal != null) {
                    viewModel.setExactValue(newVal)
                }

                val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                imm.hideSoftInputFromWindow(v.windowToken, 0)
                v.clearFocus()
                true
            } else {
                false
            }
        }

        binding.btnYearPlus.setOnClickListener { viewModel.changeYears(1) }
        binding.btnYearMinus.setOnClickListener { viewModel.changeYears(-1) }

        binding.btnMonthPlus.setOnClickListener { viewModel.changeMonths(1) }
        binding.btnMonthMinus.setOnClickListener { viewModel.changeMonths(-1) }

        binding.btnBack.setOnClickListener { finish() }

        binding.btnSaveEdit.setOnClickListener {
            val cleanStr = binding.etInvestValue.text.toString().replace(".", "").replace(",", ".")
            cleanStr.toBigDecimalOrNull()?.let { viewModel.setExactValue(it) }

            viewModel.saveEdit()
        }
    }

    private fun setupObservers() {
        val localeBR = Locale("pt", "BR")
        val currencyFormat = NumberFormat.getCurrencyInstance(localeBR)
        val numberFormat = NumberFormat.getInstance(localeBR)
        numberFormat.minimumFractionDigits = 2
        numberFormat.maximumFractionDigits = 2

        viewModel.infoRate.observe(this) { rate ->
            binding.tvRateValue.text = rate
        }

        viewModel.inputValue.observe(this) {
            if (!binding.etInvestValue.hasFocus()) {
                binding.etInvestValue.setText(numberFormat.format(it))
            }
            binding.resInvested.text = currencyFormat.format(it)
        }

        viewModel.inputYears.observe(this) { binding.txtYears.text = "$it Anos" }
        viewModel.inputMonths.observe(this) { binding.txtMonths.text = "$it Meses" }

        viewModel.resultYield.observe(this) {
            binding.resYield.text = "+ ${currencyFormat.format(it)}"
        }
        viewModel.resultTotal.observe(this) {
            binding.resTotal.text = currencyFormat.format(it)
        }

        viewModel.updateStatus.observe(this) { success ->
            if (success) {
                Toast.makeText(this, "Investimento atualizado com sucesso!", Toast.LENGTH_SHORT).show()
                finish()
            } else {
                Toast.makeText(this, "Erro ao atualizar. Tente novamente.", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
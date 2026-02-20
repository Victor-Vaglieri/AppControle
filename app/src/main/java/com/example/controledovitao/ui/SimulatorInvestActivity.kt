package com.example.controledovitao.ui

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.KeyEvent
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.controledovitao.R
import com.example.controledovitao.data.model.SimulationType
import com.example.controledovitao.databinding.SimulatorBinding
import com.example.controledovitao.ui.adapter.SimulatorOptionAdapter
import com.example.controledovitao.viewmodel.SimulatorViewModel
import java.math.BigDecimal
import java.text.NumberFormat
import java.util.Locale

class SimulatorInvestActivity : AppCompatActivity() {

    private lateinit var binding: SimulatorBinding
    private lateinit var viewModel: SimulatorViewModel

    private lateinit var optionsAdapter: SimulatorOptionAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = SimulatorBinding.inflate(layoutInflater)
        setContentView(binding.root)
        TopBarHelper.setupTopBar(this, binding.topBar)

        viewModel = ViewModelProvider(this)[SimulatorViewModel::class.java]

        setupTabs()
        setupInputs()
        setupDropdownList()
        setupObservers()
        setupNavigation()
    }

    private fun setupDropdownList() {
        optionsAdapter = SimulatorOptionAdapter(emptyList()) { selectedOption ->
            viewModel.selectOption(selectedOption)
            binding.recyclerOptions.visibility = View.GONE
            binding.spinnerInvestment.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.icon_down, 0)
        }

        binding.recyclerOptions.layoutManager = LinearLayoutManager(this)
        binding.recyclerOptions.adapter = optionsAdapter
    }

    private fun setupTabs() {
        binding.tabBanco.setOnClickListener {
            viewModel.switchType(SimulationType.BANCO)
        }
        binding.tabCripto.setOnClickListener {
            viewModel.switchType(SimulationType.CRIPTO)
        }
    }

    private fun setupInputs() {
        binding.spinnerInvestment.setOnClickListener {
            if (binding.recyclerOptions.visibility == View.VISIBLE) {
                binding.recyclerOptions.visibility = View.GONE
                binding.spinnerInvestment.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.icon_down, 0)
            } else {
                binding.recyclerOptions.visibility = View.VISIBLE
                binding.spinnerInvestment.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.icon_up, 0)
            }
        }

        binding.btnValPlus.setOnClickListener { viewModel.changeValue(BigDecimal("100")) }
        binding.btnValMinus.setOnClickListener { viewModel.changeValue(BigDecimal("-100")) }

        binding.btnYearPlus.setOnClickListener { viewModel.changeYears(1) }
        binding.btnYearMinus.setOnClickListener { viewModel.changeYears(-1) }

        binding.btnMonthPlus.setOnClickListener { viewModel.changeMonths(1) }
        binding.btnMonthMinus.setOnClickListener { viewModel.changeMonths(-1) }

        binding.txtValue.setOnEditorActionListener { v, actionId, event ->
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

        binding.btnInfo.setOnClickListener {
            val option = viewModel.selectedOption.value ?: return@setOnClickListener
            val isCripto = viewModel.currentType.value == SimulationType.CRIPTO

            val taxaFormatada = String.format("%.2f", option.annualRate * 100)

            val titulo = "Sobre: ${option.name}"
            val mensagem = if (isCripto) {
                "O ativo ${option.name} teve uma variação de $taxaFormatada% no último ano.\n\nAtenção: Criptomoedas são altamente voláteis. O histórico passado serve como base, mas não garante lucros futuros."
            } else {
                "Este investimento possui uma rentabilidade média projetada de $taxaFormatada% ao ano.\n\nEste cálculo é uma estimativa baseada nas taxas econômicas atuais (Selic, CDI)."
            }

            AlertDialog.Builder(this)
                .setTitle(titulo)
                .setMessage(mensagem)
                .setPositiveButton("Entendi", null)
                .show()
        }
    }

    private fun setupObservers() {
        val localeBR = Locale("pt", "BR")
        val currencyFormat = NumberFormat.getCurrencyInstance(localeBR)
        val numberFormat = NumberFormat.getInstance(localeBR)
        numberFormat.minimumFractionDigits = 2
        numberFormat.maximumFractionDigits = 2

        viewModel.currentType.observe(this) { type ->
            if (type == SimulationType.BANCO) {
                binding.tabBanco.setBackgroundResource(R.drawable.bg_tab_active)
                binding.tabBanco.setTextColor(Color.WHITE)
                binding.tabCripto.setBackgroundResource(R.drawable.bg_tab_inactive)
                binding.tabCripto.setTextColor(Color.GRAY)
            } else {
                binding.tabCripto.setBackgroundResource(R.drawable.bg_tab_active)
                binding.tabCripto.setTextColor(Color.WHITE)
                binding.tabBanco.setBackgroundResource(R.drawable.bg_tab_inactive)
                binding.tabBanco.setTextColor(Color.GRAY)
            }
        }

        viewModel.availableOptions.observe(this) { options ->
            optionsAdapter.updateList(options)

            if (options.isNotEmpty()) {
                binding.spinnerInvestment.text = options[0].name
                viewModel.selectOption(options[0])
            }
        }

        viewModel.selectedOption.observe(this) { option ->
            binding.spinnerInvestment.text = option.name
        }

        viewModel.infoText.observe(this) { text ->
            binding.tvLabelYield.text = text
        }
        viewModel.infoRate.observe(this) { rate ->
            binding.tvRateValue.text = rate
        }

        viewModel.inputValue.observe(this) {
            if (!binding.txtValue.hasFocus()) {
                binding.txtValue.setText(numberFormat.format(it))
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
    }

    private fun setupNavigation() {
        binding.btnGoToInvestments.setOnClickListener {
            startActivity(Intent(this, InvestmentsActivity::class.java))
            finish()
        }

        binding.btnLaunch.setOnClickListener {
            // TODO: Logica para salvar investimento real
        }
    }
}
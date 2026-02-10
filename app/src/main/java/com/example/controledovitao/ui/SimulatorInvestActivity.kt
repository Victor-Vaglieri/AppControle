package com.example.controledovitao.ui

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import com.example.controledovitao.R
import com.example.controledovitao.data.model.SimulationType
import com.example.controledovitao.databinding.SimulatorBinding
import com.example.controledovitao.viewmodel.SimulatorViewModel
import com.example.controledovitao.ui.adapter.SimulatorOptionAdapter
import androidx.recyclerview.widget.LinearLayoutManager
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
        // Lógica de Expandir/Recolher ao clicar no campo "Spinner"
        binding.spinnerInvestment.setOnClickListener {
            if (binding.recyclerOptions.visibility == View.VISIBLE) {
                // Fechar
                binding.recyclerOptions.visibility = View.GONE
                binding.spinnerInvestment.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.icon_down, 0)
            } else {
                // Abrir
                binding.recyclerOptions.visibility = View.VISIBLE
                binding.spinnerInvestment.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.icon_up, 0) // Use seu icon_up
            }
        }

        // Inputs de Valor e Data (Mantidos iguais)
        binding.btnValPlus.setOnClickListener { viewModel.changeValue(BigDecimal("100")) }
        binding.btnValMinus.setOnClickListener { viewModel.changeValue(BigDecimal("-100")) }

        binding.btnYearPlus.setOnClickListener { viewModel.changeYears(1) }
        binding.btnYearMinus.setOnClickListener { viewModel.changeYears(-1) }

        binding.btnMonthPlus.setOnClickListener { viewModel.changeMonths(1) }
        binding.btnMonthMinus.setOnClickListener { viewModel.changeMonths(-1) }
    }
    private fun setupObservers() {
        val localeBR = Locale("pt", "BR")
        val currencyFormat = NumberFormat.getCurrencyInstance(localeBR)
        val numberFormat = NumberFormat.getInstance(localeBR)

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

        // 3. Atualizar Textos de Info
        viewModel.infoText.observe(this) { text ->
            binding.tvLabelYield.text = text
        }
        viewModel.infoRate.observe(this) { rate ->
            binding.tvRateValue.text = rate
        }

        // 4. Atualizar Inputs
        viewModel.inputValue.observe(this) {
            binding.txtValue.text = numberFormat.format(it)
            binding.resInvested.text = currencyFormat.format(it)
        }
        viewModel.inputYears.observe(this) { binding.txtYears.text = "$it Anos" }
        viewModel.inputMonths.observe(this) { binding.txtMonths.text = "$it Meses" }

        // 5. Resultados
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
            finish() // Opcional, se quiser fechar o simulador
        }

        binding.btnLaunch.setOnClickListener {
            // TODO: Logica para salvar investimento real
        }
    }
}
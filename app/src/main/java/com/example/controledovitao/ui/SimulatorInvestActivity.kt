package com.example.controledovitao.ui

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.widget.PopupMenu // Para o Dropdown
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import com.example.controledovitao.R
import com.example.controledovitao.data.repository.SimulationType
import com.example.controledovitao.databinding.SimulatorBinding
import com.example.controledovitao.viewmodel.SimulatorViewModel
import java.math.BigDecimal
import java.text.NumberFormat
import java.util.Locale

class SimulatorInvestActivity : AppCompatActivity() {

    private lateinit var binding: SimulatorBinding
    private lateinit var viewModel: SimulatorViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = SimulatorBinding.inflate(layoutInflater)
        setContentView(binding.root)
        TopBarHelper.setupTopBar(this, binding.topBar)

        viewModel = ViewModelProvider(this)[SimulatorViewModel::class.java]

        setupTabs()
        setupInputs()
        setupObservers()
        setupNavigation()
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
        // Dropdown (PopupMenu)
        binding.spinnerInvestment.setOnClickListener { view ->
            val options = viewModel.availableOptions.value ?: return@setOnClickListener
            val popup = PopupMenu(this, view)

            options.forEach { option ->
                popup.menu.add(option.name).setOnMenuItemClickListener {
                    viewModel.selectOption(option)
                    true
                }
            }
            popup.show()
        }

        // Valor
        binding.btnValPlus.setOnClickListener { viewModel.changeValue(BigDecimal("100")) }
        binding.btnValMinus.setOnClickListener { viewModel.changeValue(BigDecimal("-100")) }

        // Anos
        binding.btnYearPlus.setOnClickListener { viewModel.changeYears(1) }
        binding.btnYearMinus.setOnClickListener { viewModel.changeYears(-1) }

        // Meses
        binding.btnMonthPlus.setOnClickListener { viewModel.changeMonths(1) }
        binding.btnMonthMinus.setOnClickListener { viewModel.changeMonths(-1) }
    }

    private fun setupObservers() {
        val localeBR = Locale("pt", "BR")
        val currencyFormat = NumberFormat.getCurrencyInstance(localeBR)
        val numberFormat = NumberFormat.getInstance(localeBR)

        // 1. Alternar visual das abas
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

        viewModel.selectedOption.observe(this) { option ->
            binding.spinnerInvestment.text = option.name
        }

        // 2. Atualizar Dropdown Label
        // (Isso depende de como você expõe o selecionado, simplificado aqui)
        viewModel.availableOptions.observe(this) { options ->
            if (options.isNotEmpty()) binding.spinnerInvestment.text = options[0].name
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
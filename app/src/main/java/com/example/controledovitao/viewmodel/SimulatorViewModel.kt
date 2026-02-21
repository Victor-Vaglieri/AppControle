package com.example.controledovitao.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.controledovitao.data.model.Invest
import com.example.controledovitao.data.repository.InvestmentsRepository
import com.example.controledovitao.data.model.SimulationOption
import com.example.controledovitao.data.model.SimulationType
import com.example.controledovitao.data.repository.SimulationRepository
import kotlinx.coroutines.launch
import java.math.BigDecimal
import kotlin.math.pow

class SimulatorViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = SimulationRepository(application)
    private val investRepo = InvestmentsRepository

    private val _currentType = MutableLiveData(SimulationType.BANCO)
    val currentType: LiveData<SimulationType> = _currentType

    private val _availableOptions = MutableLiveData<List<SimulationOption>>()
    val availableOptions: LiveData<List<SimulationOption>> = _availableOptions

    private val _selectedOption = MutableLiveData<SimulationOption>()
    val selectedOption: LiveData<SimulationOption> = _selectedOption

    val inputValue = MutableLiveData(BigDecimal("1500.00"))
    val inputYears = MutableLiveData(1)
    val inputMonths = MutableLiveData(0)

    private val _resultTotal = MutableLiveData<BigDecimal>()
    val resultTotal: LiveData<BigDecimal> = _resultTotal

    private val _resultYield = MutableLiveData<BigDecimal>()
    val resultYield: LiveData<BigDecimal> = _resultYield

    private val _infoText = MutableLiveData<String>()
    val infoText: LiveData<String> = _infoText

    private val _infoRate = MutableLiveData<String>()
    val infoRate: LiveData<String> = _infoRate

    // --- ALTERAÇÃO AQUI: LiveData para observar o status de salvamento ---
    private val _saveStatus = MutableLiveData<Boolean>()
    val saveStatus: LiveData<Boolean> = _saveStatus

    init {
        loadOptions(SimulationType.BANCO)
    }

    fun switchType(type: SimulationType) {
        _currentType.value = type
        loadOptions(type)
    }

    private fun loadOptions(type: SimulationType) {
        viewModelScope.launch {
            val optionsEncontradas = repository.getOptions(type)

            _availableOptions.value = optionsEncontradas

            if (optionsEncontradas.isNotEmpty()) {
                selectOption(optionsEncontradas[0])
            } else {
                _infoText.value = "Nenhuma opção encontrada."
                _infoRate.value = "-"
            }
        }
    }

    fun selectOption(option: SimulationOption) {
        _selectedOption.value = option

        val taxaFormatada = String.format("%.2f", option.annualRate * 100)
        _infoRate.value = "$taxaFormatada% a.a."

        if (option.type == SimulationType.CRIPTO) {
            _infoText.value = "Rendimento no último ano"
        } else {
            _infoText.value = "Rentabilidade anual projetada"
            if (option.name.contains("CDB", true)) _infoText.value = "Taxa CDI anual"
        }

        calculate()
    }

    fun changeValue(delta: BigDecimal) {
        val current = inputValue.value ?: BigDecimal.ZERO
        val newValue = current.add(delta).max(BigDecimal.ZERO)
        inputValue.value = newValue
        calculate()
    }

    fun changeYears(delta: Int) {
        val current = inputYears.value ?: 0
        inputYears.value = (current + delta).coerceAtLeast(0)
        calculate()
    }

    fun changeMonths(delta: Int) {
        val current = inputMonths.value ?: 0
        inputMonths.value = (current + delta).coerceIn(0, 11)
        calculate()
    }

    fun setExactValue(value: BigDecimal) {
        val safeValue = value.max(BigDecimal.ZERO)
        inputValue.value = safeValue
        calculate()
    }

    private fun calculate() {
        val principal = inputValue.value ?: BigDecimal.ZERO
        val years = inputYears.value ?: 0
        val months = inputMonths.value ?: 0
        val option = _selectedOption.value ?: return

        if (principal <= BigDecimal.ZERO) {
            _resultTotal.value = BigDecimal.ZERO
            _resultYield.value = BigDecimal.ZERO
            return
        }

        val annualRate = option.annualRate
        val monthlyRate = (1 + annualRate).pow(1.0 / 12.0) - 1
        val totalMonths = (years * 12) + months

        val factor = (1 + monthlyRate).pow(totalMonths.toDouble())
        val finalAmount = principal.multiply(BigDecimal(factor))

        _resultTotal.value = finalAmount
        _resultYield.value = finalAmount.subtract(principal)
    }

    fun saveInvestment() {
        val option = _selectedOption.value ?: return
        val amount = inputValue.value ?: BigDecimal.ZERO
        val totalEstimate = _resultTotal.value ?: BigDecimal.ZERO
        val years = inputYears.value ?: 0
        val months = inputMonths.value ?: 0

        if (amount <= BigDecimal.ZERO) {
            _saveStatus.value = false
            return
        }
        val periodString = "P${years}Y${months}M"
        val newInvestment = Invest(
            id = "",
            name = option.name,
            value = amount.toDouble(),
            spentDate = System.currentTimeMillis(),
            period = periodString,
            estimate = totalEstimate.toDouble()
        )

        investRepo.saveInvestment(newInvestment) { success ->
            _saveStatus.value = success
        }
    }
}
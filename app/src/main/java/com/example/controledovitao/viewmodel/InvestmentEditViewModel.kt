package com.example.controledovitao.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.controledovitao.data.model.Invest
import com.example.controledovitao.data.model.SimulationOption
import com.example.controledovitao.data.model.SimulationType
import com.example.controledovitao.data.repository.InvestmentsRepository
import com.example.controledovitao.data.repository.SimulationRepository
import kotlinx.coroutines.launch
import java.math.BigDecimal
import kotlin.math.pow

class InvestmentEditViewModel(application: Application) : AndroidViewModel(application) {

    private val investRepo = InvestmentsRepository
    private val simRepo = SimulationRepository(application) // Usado para buscar a taxa (rate) do ativo
    private var investId: String = ""
    private var investName: String = ""
    private var investDate: Long = 0L

    private val _selectedOption = MutableLiveData<SimulationOption?>()

    val inputValue = MutableLiveData(BigDecimal.ZERO)
    val inputYears = MutableLiveData(0)
    val inputMonths = MutableLiveData(0)

    private val _resultTotal = MutableLiveData<BigDecimal>()
    val resultTotal: LiveData<BigDecimal> = _resultTotal

    private val _resultYield = MutableLiveData<BigDecimal>()
    val resultYield: LiveData<BigDecimal> = _resultYield

    private val _infoRate = MutableLiveData<String>()
    val infoRate: LiveData<String> = _infoRate

    private val _updateStatus = MutableLiveData<Boolean>()
    val updateStatus: LiveData<Boolean> = _updateStatus

    fun initData(id: String, name: String, value: Double, periodStr: String) {
        investId = id
        investName = name
        inputValue.value = BigDecimal.valueOf(value)

        parsePeriod(periodStr)
        fetchOptionRate(name)
    }

    private fun parsePeriod(period: String) {
        var y = 0
        var m = 0
        if (period.startsWith("P")) {
            val yIndex = period.indexOf('Y')
            val mIndex = period.indexOf('M')
            if (yIndex > 0) {
                y = period.substring(1, yIndex).toIntOrNull() ?: 0
            }
            if (mIndex > 0) {
                val start = if (yIndex > 0) yIndex + 1 else 1
                m = period.substring(start, mIndex).toIntOrNull() ?: 0
            }
        }
        inputYears.value = y
        inputMonths.value = m
    }

    private fun fetchOptionRate(name: String) {
        viewModelScope.launch {
            val bancos = simRepo.getOptions(SimulationType.BANCO)
            val criptos = simRepo.getOptions(SimulationType.CRIPTO)

            val option = (bancos + criptos).find { it.name.equals(name, ignoreCase = true) }

            if (option != null) {
                _selectedOption.value = option
                val taxaFormatada = String.format("%.2f", option.annualRate * 100)
                _infoRate.value = "$taxaFormatada% a.a."
            } else {
                _infoRate.value = "Taxa Indisponível"
            }
            calculate()
        }
    }

    // --- Controles de Input ---
    fun changeValue(delta: BigDecimal) {
        val current = inputValue.value ?: BigDecimal.ZERO
        inputValue.value = current.add(delta).max(BigDecimal.ZERO)
        calculate()
    }

    fun setExactValue(value: BigDecimal) {
        inputValue.value = value.max(BigDecimal.ZERO)
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

    private fun calculate() {
        val principal = inputValue.value ?: BigDecimal.ZERO
        val years = inputYears.value ?: 0
        val months = inputMonths.value ?: 0
        val option = _selectedOption.value

        if (principal <= BigDecimal.ZERO || option == null) {
            _resultTotal.value = principal
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

    fun saveEdit() {
        val amount = inputValue.value ?: BigDecimal.ZERO
        val totalEstimate = _resultTotal.value ?: BigDecimal.ZERO
        val years = inputYears.value ?: 0
        val months = inputMonths.value ?: 0

        if (amount <= BigDecimal.ZERO || investId.isEmpty()) {
            _updateStatus.value = false
            return
        }

        val periodString = "P${years}Y${months}M"

        val updatedInvestment = Invest(
            id = investId,
            name = investName,
            value = amount.toDouble(),
            spentDate = System.currentTimeMillis(),
            period = periodString,
            estimate = totalEstimate.toDouble()
        )

        investRepo.updateInvestment(updatedInvestment) { success ->
            _updateStatus.value = success
        }
    }
}
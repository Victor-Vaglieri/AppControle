package com.example.controledovitao.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.controledovitao.data.model.Invest
import com.example.controledovitao.data.repository.InvestmentsRepository
import java.math.BigDecimal

class InvestmentsViewModel : ViewModel() {
    private val repository = InvestmentsRepository()
    private val _investmentsList = MutableLiveData<List<Invest>>()
    val investmentsList: LiveData<List<Invest>> = _investmentsList

    private val _totalInvested = MutableLiveData(BigDecimal.ZERO)
    val totalInvested: LiveData<BigDecimal> = _totalInvested

    private val _totalEstimated = MutableLiveData(BigDecimal.ZERO)
    val totalEstimated: LiveData<BigDecimal> = _totalEstimated

    private val _totalProfit = MutableLiveData(BigDecimal.ZERO)
    val totalProfit: LiveData<BigDecimal> = _totalProfit

    init {
        startListening()
    }

    private fun startListening() {
        repository.listenToInvestments { list ->
            _investmentsList.value = list
            calculateTotals(list)
        }
    }

    private fun calculateTotals(list: List<Invest>) {
        var invested = BigDecimal.ZERO
        var estimated = BigDecimal.ZERO

        list.forEach { item ->
            invested = invested.add(item.valueAsBigDecimal)
            estimated = estimated.add(item.estimateAsBigDecimal)
        }

        _totalInvested.value = invested
        _totalEstimated.value = estimated
        _totalProfit.value = estimated.subtract(invested)
    }

}
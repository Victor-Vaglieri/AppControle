package com.example.controledovitao.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.controledovitao.data.model.Invest
import com.example.controledovitao.data.repository.InvestmentsRepository

class InvestmentsViewModel : ViewModel() {
    private val repository = InvestmentsRepository()
    private val _investmentsList = MutableLiveData<List<Invest>>()
    val investmentsList: LiveData<List<Invest>> = _investmentsList

    init {
        loadInvestments()
    }
    private fun loadInvestments() {
        val data = repository.getInvestmentsList()
        _investmentsList.value = data
    }
}
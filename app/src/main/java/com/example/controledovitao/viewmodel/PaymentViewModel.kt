package com.example.controledovitao.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.controledovitao.data.model.Options
import com.example.controledovitao.data.model.Payment
import com.example.controledovitao.data.repository.OverviewRepository
import java.math.BigDecimal

class PaymentViewModel : ViewModel() {

    private val repository = OverviewRepository()

    private val _paymentMethods = MutableLiveData<List<Payment>>()
    val paymentMethods: LiveData<List<Payment>> = _paymentMethods

    private val _selectedPayment = MutableLiveData<Payment?>()
    val selectedPayment: LiveData<Payment?> = _selectedPayment

    fun loadMethods() {
        _paymentMethods.value = repository.getMethods()
    }

    fun loadMethodByName(name: String) {
        _selectedPayment.value = repository.getMethodByName(name)
    }

    fun createPayment(name: String, type: String, limit: Double, balance: Double, closeDay: Int, dueDay: Int) {
        val typeEnum = if (type == "Crédito") Options.CREDIT else Options.DEBIT

        val newPayment = Payment(
            name = name,
            optionType = typeEnum.toString(),
            balance = BigDecimal(balance).toDouble(),
            limit = BigDecimal(limit).toDouble(),
            usage = BigDecimal.ZERO.toDouble(),
            bestDate = closeDay,
            shutdown = dueDay,
            spent = mutableListOf()
        )
        repository.addMethod(newPayment)
    }

    fun updatePayment(originalName: String, name: String, limit: Double, closeDay: Int, dueDay: Int) {
        val current = repository.getMethodByName(originalName) ?: return

        val updatedPayment = current.copy(
            name = name,
            limit = BigDecimal(limit).toDouble(),
            bestDate = closeDay,
            shutdown = dueDay
        )
        repository.updateMethod(originalName, updatedPayment)
    }
}
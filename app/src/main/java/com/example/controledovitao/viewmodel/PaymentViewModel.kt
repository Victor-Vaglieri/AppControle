package com.example.controledovitao.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.controledovitao.data.model.Options
import com.example.controledovitao.data.model.Payment
import com.example.controledovitao.data.repository.PaymentRepository
import java.math.BigDecimal

class PaymentViewModel : ViewModel() {

    private val repository = PaymentRepository()

    private val _paymentMethods = MutableLiveData<List<Payment>>()
    val paymentMethods: LiveData<List<Payment>> = _paymentMethods

    private val _selectedPayment = MutableLiveData<Payment?>()
    val selectedPayment: LiveData<Payment?> = _selectedPayment

    private val _operationStatus = MutableLiveData<Boolean>()
    val operationStatus: LiveData<Boolean> = _operationStatus

    init {
        loadMethods()
    }

    fun loadMethods() {
        repository.listenToMethods { list ->
            _paymentMethods.value = list
        }
    }

    fun loadMethodByName(name: String) {
        val list = _paymentMethods.value ?: emptyList()
        val found = list.find { it.name == name }
        _selectedPayment.value = found
    }

    fun createPayment(name: String, type: String, limit: Double, balance: Double, closeDay: Int, dueDay: Int) {
        val typeEnum = if (type.equals("Crédito", ignoreCase = true)) Options.CREDIT else Options.DEBIT

        val newPayment = Payment(
            name = name,
            optionType = typeEnum.name,
            balance = balance,
            limit = limit,
            usage = 0.0,
            bestDate = closeDay,
            shutdown = dueDay,
            spent = mutableListOf()
        )

        repository.saveMethod(newPayment) { success ->
            _operationStatus.value = success
        }
    }

    fun updatePayment(originalName: String, name: String, limit: Double, closeDay: Int, dueDay: Int) {
        val currentList = _paymentMethods.value ?: emptyList()
        val originalPayment = currentList.find { it.name == originalName }

        if (originalPayment == null) {
            _operationStatus.value = false
            return
        }


        val updatedPayment = originalPayment.copy(
            name = name,
            limit = limit,
            bestDate = closeDay,
            shutdown = dueDay

        )

        repository.updateMethod(updatedPayment) { success ->
            _operationStatus.value = success
        }
    }
}
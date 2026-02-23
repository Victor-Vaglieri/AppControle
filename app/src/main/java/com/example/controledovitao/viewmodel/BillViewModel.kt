package com.example.controledovitao.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.controledovitao.data.repository.PaymentRepository

class BillViewModel : ViewModel() {

    private val paymentRepo = PaymentRepository

    private val _closeStatus = MutableLiveData<Boolean>()
    val closeStatus: LiveData<Boolean> = _closeStatus

    fun confirmInvoiceClose(paymentId: String) {
        if (paymentId.isEmpty()) {
            _closeStatus.value = false
            return
        }

        paymentRepo.closeInvoice(paymentId) { success ->
            _closeStatus.value = success
        }
    }
}
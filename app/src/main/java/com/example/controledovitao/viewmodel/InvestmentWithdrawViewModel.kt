package com.example.controledovitao.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.controledovitao.data.model.Invest
import com.example.controledovitao.data.model.Payment
import com.example.controledovitao.data.repository.InvestmentsRepository
import com.example.controledovitao.data.repository.PaymentRepository
import java.math.BigDecimal

class InvestmentWithdrawViewModel : ViewModel() {

    private val investRepo = InvestmentsRepository
    private val paymentRepo = PaymentRepository

    private var investId: String = ""
    private var investName: String = ""
    private var baseValue = BigDecimal.ZERO

    val estimatedTotal = MutableLiveData(BigDecimal.ZERO)
    val withdrawValue = MutableLiveData(BigDecimal.ZERO)

    private val _availablePayments = MutableLiveData<List<Payment>>()
    val availablePayments: LiveData<List<Payment>> = _availablePayments

    private val _selectedPayment = MutableLiveData<Payment?>()
    val selectedPayment: LiveData<Payment?> = _selectedPayment

    private val _withdrawStatus = MutableLiveData<Boolean>()
    val withdrawStatus: LiveData<Boolean> = _withdrawStatus

    init {
        paymentRepo.listenToMethods { payments ->
            _availablePayments.value = payments
            if (payments.isNotEmpty() && _selectedPayment.value == null) {
                _selectedPayment.value = payments[0]
            }
        }
    }

    fun initData(id: String, name: String, value: Double, estimated: Double) {
        investId = id
        investName = name
        baseValue = BigDecimal.valueOf(value)

        val est = BigDecimal.valueOf(estimated)
        estimatedTotal.value = est
        withdrawValue.value = est
    }

    fun changeWithdrawValue(delta: BigDecimal) {
        val current = withdrawValue.value ?: BigDecimal.ZERO
        val maxAvailable = estimatedTotal.value ?: BigDecimal.ZERO
        withdrawValue.value = current.add(delta).coerceIn(BigDecimal.ZERO, maxAvailable)
    }

    fun setExactWithdrawValue(value: BigDecimal) {
        val maxAvailable = estimatedTotal.value ?: BigDecimal.ZERO
        withdrawValue.value = value.coerceIn(BigDecimal.ZERO, maxAvailable)
    }

    fun selectPayment(payment: Payment) {
        _selectedPayment.value = payment
    }

    fun confirmWithdrawal() {
        val amountToWithdraw = withdrawValue.value ?: BigDecimal.ZERO
        val destPayment = _selectedPayment.value

        if (amountToWithdraw <= BigDecimal.ZERO || destPayment == null || investId.isEmpty()) {
            _withdrawStatus.value = false
            return
        }

        val currentTotal = estimatedTotal.value ?: BigDecimal.ZERO

        val newBalance = destPayment.balance + amountToWithdraw.toDouble()
        val updatedPayment = destPayment.copy(balance = newBalance)

        paymentRepo.updateMethod(updatedPayment) { successPay ->
            if (!successPay) {
                _withdrawStatus.value = false
                return@updateMethod
            }

            if (amountToWithdraw >= currentTotal.subtract(BigDecimal("0.1"))) {
                investRepo.deleteInvestment(investId)
                _withdrawStatus.value = true
            } else {
                val newBaseValue = baseValue.subtract(amountToWithdraw).coerceAtLeast(BigDecimal.ZERO)

                val updatedInvest = Invest(
                    id = investId,
                    name = investName,
                    value = newBaseValue.toDouble(),
                )

                investRepo.updateInvestment(updatedInvest) { successInv ->
                    _withdrawStatus.value = successInv
                }
            }
        }
    }
}
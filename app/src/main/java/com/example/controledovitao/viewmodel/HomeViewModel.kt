package com.example.controledovitao.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.controledovitao.data.model.Options
import com.example.controledovitao.data.model.Payment
import com.example.controledovitao.data.model.Spent
import com.example.controledovitao.data.repository.InvestmentsRepository
import com.example.controledovitao.data.repository.PaymentRepository
import java.math.BigDecimal
import java.util.Calendar

class HomeViewModel : ViewModel() {

    private val paymentRepo = PaymentRepository
    private val investRepo = InvestmentsRepository
    private val _totalBalance = MutableLiveData(BigDecimal.ZERO)
    val totalBalance: LiveData<BigDecimal> = _totalBalance

    private val _totalLimit = MutableLiveData(BigDecimal.ZERO)
    val totalLimit: LiveData<BigDecimal> = _totalLimit

    private val _totalUsage = MutableLiveData(BigDecimal.ZERO)
    val totalUsage: LiveData<BigDecimal> = _totalUsage

    private val _totalInvest = MutableLiveData(BigDecimal.ZERO)
    val totalInvest: LiveData<BigDecimal> = _totalInvest

    private val _methodNames = MutableLiveData<List<String>>()
    val methodNames: LiveData<List<String>> = _methodNames

    private val _recentSpents = MutableLiveData<List<Triple<String, Pair<BigDecimal, Int>, Spent>>>()
    val recentSpents: LiveData<List<Triple<String, Pair<BigDecimal, Int>, Spent>>> = _recentSpents

    private val _bestCardName = MutableLiveData<String>()
    val bestCardName: LiveData<String> = _bestCardName

    private val _errorMessage = MutableLiveData<String>()
    val errorMessage: LiveData<String> = _errorMessage
    private var cachedPayments = listOf<Payment>()
    private var allSpentsCache = listOf<Triple<String, Pair<BigDecimal, Int>, Spent>>()

    private val _selectedPaymentMethod = MutableLiveData<Payment?>()
    val selectedPaymentMethod: LiveData<Payment?> = _selectedPaymentMethod

    init {
        startListeningPayments()
        startListeningInvestments()
    }

    private fun startListeningPayments() {
        paymentRepo.listenToMethods { payments ->
            cachedPayments = payments

            if (payments.isEmpty()) {
                _errorMessage.value = "Nenhum método encontrado"
                _totalLimit.value = BigDecimal.ZERO
                _totalBalance.value = BigDecimal.ZERO
            } else {
                calculatePaymentTotals(payments)
                processSpents(payments)
                extractMethodNames(payments)
                findBestCard(payments)

                val currentSelection = _selectedPaymentMethod.value
                if (currentSelection != null) {
                    val updatedVersion = payments.find { it.id == currentSelection.id }
                    if (updatedVersion != null) {
                        _selectedPaymentMethod.value = updatedVersion
                    } else {
                        selectMethod("TODOS")
                    }
                }
            }
        }
    }

    private fun startListeningInvestments() {
        investRepo.listenToInvestments { investments ->
            val total = investments.sumOf { it.value }
            _totalInvest.value = BigDecimal.valueOf(total)
        }
    }

    fun selectMethod(methodName: String) {
        if (methodName.equals("TODOS", ignoreCase = true)) {
            _selectedPaymentMethod.value = null
            filterSpents("TODOS")
        } else {
            val found = cachedPayments.find { it.name.equals(methodName, ignoreCase = true) }
            if (found != null) {
                _selectedPaymentMethod.value = found
                filterSpents(methodName)
            }
        }
    }

    fun filterSpents(methodName: String) {
        if (methodName.equals("TODOS", ignoreCase = true)) {
            _recentSpents.value = allSpentsCache.take(10)
        } else {
            val filtered = allSpentsCache.filter { it.first.equals(methodName, ignoreCase = true) }
            _recentSpents.value = filtered
        }
    }

    fun updateSelectedCardLimit(delta: Double) {
        val currentCard = _selectedPaymentMethod.value ?: return
        val newLimit = ((currentCard.limit ?: 0.0) + delta).coerceAtLeast(0.0)

        val updatedCard = currentCard.copy(limit = newLimit)
        paymentRepo.updateMethod(updatedCard) { }
    }

    fun updateSelectedCardBalance(delta: Double) {
        val currentCard = _selectedPaymentMethod.value ?: return
        val newBalance = currentCard.balance + delta

        val updatedCard = currentCard.copy(balance = newBalance)
        paymentRepo.updateMethod(updatedCard) { }
    }

    fun closeInvoice() {
        val currentCard = _selectedPaymentMethod.value ?: return
        val updatedCard = currentCard.copy(usage = 0.0)

        paymentRepo.updateMethod(updatedCard) { success ->
            if (success) _errorMessage.value = "Fatura fechada!"
        }
    }

    private fun calculatePaymentTotals(payments: List<Payment>) {
        var saldo = BigDecimal.ZERO
        var limite = BigDecimal.ZERO
        var uso = BigDecimal.ZERO

        payments.forEach { pay ->
            saldo = saldo.add(pay.balanceAsBigDecimal)
            if (pay.option == Options.CREDIT) {
                pay.limitAsBigDecimal?.let { limite = limite.add(it) }
                pay.usageAsBigDecimal?.let { uso = uso.add(it) }
            }
        }
        _totalBalance.value = saldo
        _totalLimit.value = limite
        _totalUsage.value = uso
    }

    private fun processSpents(payments: List<Payment>) {
        val tempList = mutableListOf<Triple<String, Pair<BigDecimal, Int>, Spent>>()
        payments.forEach { pay ->
            pay.spent.forEach { spent ->
                val subtitle = if (spent.times > 1) {
                    Pair(spent.valueAsBigDecimal, spent.times)
                } else {
                    Pair(spent.valueAsBigDecimal, 0)
                }
                tempList.add(Triple(pay.name, subtitle, spent))
            }
        }
        allSpentsCache = tempList.sortedByDescending { it.third.spentDate }

        val currentName = _selectedPaymentMethod.value?.name ?: "TODOS"
        filterSpents(currentName)
    }

    private fun extractMethodNames(payments: List<Payment>) {
        _methodNames.value = payments.map { it.name }
    }

    private fun findBestCard(payments: List<Payment>) {
        val today = Calendar.getInstance().get(Calendar.DAY_OF_MONTH)
        val creditCards = payments.filter { it.option == Options.CREDIT }

        if (creditCards.isEmpty()) {
            _bestCardName.value = "Sem cartões"
            return
        }

        val bestWindow = creditCards.filter { card ->
            val fechamento = card.bestDate ?: 32
            val vencimento = card.shutdown ?: 0
            today >= fechamento && today < vencimento
        }

        val winner = if (bestWindow.isNotEmpty()) {
            bestWindow.maxByOrNull { (it.limit ?: 0.0) - (it.usage ?: 0.0) }
        } else {
            creditCards.maxByOrNull { card ->
                val fechamento = card.bestDate ?: 0
                if (fechamento > today) fechamento - today else 30 + (fechamento - today)
            }
        }
        _bestCardName.value = winner?.name ?: "Indisponível"
    }
}
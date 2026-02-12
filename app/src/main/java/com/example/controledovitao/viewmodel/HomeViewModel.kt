package com.example.controledovitao.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.controledovitao.data.model.Options
import com.example.controledovitao.data.model.Payment
import com.example.controledovitao.data.model.Spent
import com.example.controledovitao.data.repository.PaymentRepository
import java.math.BigDecimal
import java.util.Calendar

class HomeViewModel : ViewModel() {

    private val paymentRepo = PaymentRepository

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

    private var allSpentsCache = listOf<Triple<String, Pair<BigDecimal, Int>, Spent>>()

    init {
        startListening()
    }

    private fun startListening() {
        paymentRepo.listenToMethods { payments ->
            if (payments.isEmpty()) {
                _errorMessage.value = "Nenhum método de pagamento encontrado"
                _totalLimit.value = BigDecimal.ZERO
                _totalBalance.value = BigDecimal.ZERO
            } else {
                Log.d("HomeViewModel", "Atualizando totais para ${payments.size} métodos")
                calculateTotals(payments)
                processSpents(payments)
                extractMethodNames(payments)
                findBestCard(payments)
            }
        }
    }

    private fun calculateTotals(payments: List<Payment>) {
        var saldo = BigDecimal.ZERO
        var limite = BigDecimal.ZERO
        var uso = BigDecimal.ZERO

        payments.forEach { pay ->
            Log.d("CalcTotal", "Processando: ${pay.name} | Tipo: ${pay.option} | Limite: ${pay.limit}")

            saldo = saldo.add(pay.balanceAsBigDecimal)

            if (pay.option == Options.CREDIT) {
                pay.limitAsBigDecimal?.let { limite = limite.add(it) }
                pay.usageAsBigDecimal?.let { uso = uso.add(it) }
            }
        }

        _totalBalance.value = saldo
        _totalLimit.value = limite
        _totalUsage.value = uso
        // _totalInvest.value = ... (Futuro)
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

        val sorted = tempList.sortedByDescending { it.third.spentDate }
        allSpentsCache = sorted
        _recentSpents.value = sorted.take(10)
    }

    private fun extractMethodNames(payments: List<Payment>) {
        val names = payments.map { it.name }
        _methodNames.value = names
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
    
    fun filterSpents(methodName: String) {
        if (methodName.equals("TODOS", ignoreCase = true)) {
            _recentSpents.value = allSpentsCache.take(10)
        } else {
            val filtered = allSpentsCache.filter { it.first.equals(methodName, ignoreCase = true) }
            _recentSpents.value = filtered
        }
    }
}
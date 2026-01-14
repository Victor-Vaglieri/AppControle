package com.example.controledovitao.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.controledovitao.data.model.Spent
import com.example.controledovitao.data.repository.OverviewRepository
import java.math.BigDecimal
import java.text.NumberFormat
import java.util.Locale


class HomeViewModel : ViewModel() {

    private val repository = OverviewRepository()

    private val _getInfosSuccess = MutableLiveData<Boolean>()
    val getInfosSuccess: LiveData<Boolean> = _getInfosSuccess

    var invest: BigDecimal = BigDecimal.ZERO
        private set

    var methods: List<String> = emptyList()
        private set
    var balance: BigDecimal = BigDecimal.ZERO
        private set
    var limit: BigDecimal = BigDecimal.ZERO
        private set
    var usage: BigDecimal = BigDecimal.ZERO
        private set

    private val _spentItems =
        MutableLiveData<List<Triple<String, String, Spent>>>()

    val spentItems: LiveData<List<Triple<String, String, Spent>>> = _spentItems

    private val _errorMessage = MutableLiveData<String>()
    val errorMessage: LiveData<String> = _errorMessage

    init {
        findBalance()
        findSpents()
        findMethods()
    }

    private fun findMethods() {
        val aux = repository.getMethods()
        if (aux.isNotEmpty()) {
            methods = aux.map { item -> item.name }
            _getInfosSuccess.value = true
        } else {
            _errorMessage.value = "Nenhum método de pagamento encontrado"
        }
    }

    private fun findBalance() {
        val resposta = repository.getBalance()

        if (resposta.isNotEmpty()) {
            _getInfosSuccess.value = true
            invest = resposta[0]
            balance = resposta[1]
            limit = resposta[2]
            usage = resposta[3]
        } else {
            _errorMessage.value = "Erro ao acessar os valores"
        }
    }

    private fun correctString(number: BigDecimal): String {
        val localeBR = Locale.of("pt", "BR")

        val formatator = NumberFormat.getNumberInstance(localeBR)

        formatator.minimumFractionDigits = 2
        formatator.maximumFractionDigits = 2

        val transform = formatator.format(number)

        return transform
    }

    private fun findSpents() {
        val spents = repository.getSpents()
        if (spents.isNotEmpty()) {
            val result = spents.map { spent ->
                val title = spent.name

                val subtitle = if (spent.times != null && spent.times > 1) {
                    "${correctString(spent.value)} x ${spent.times}"
                } else {
                    correctString(spent.value)
                }

                Triple(title, subtitle, spent)
            }

            _spentItems.value = result
        } else {
            _errorMessage.value = "Nenhum gasto encontrado"
        }
    }

}

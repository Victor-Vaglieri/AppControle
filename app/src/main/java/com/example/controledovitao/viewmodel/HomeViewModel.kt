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

    private val _bestCardName = MutableLiveData<String>()
    val bestCardName: LiveData<String> = _bestCardName

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
        MutableLiveData<List<Triple<String, Pair<BigDecimal,Int>, Spent>>>()

    val spentItems: LiveData<List<Triple<String,  Pair<BigDecimal,Int>, Spent>>> = _spentItems

    private val _errorMessage = MutableLiveData<String>()
    val errorMessage: LiveData<String> = _errorMessage

    init {
        findBalance()
        findSpents()
        findMethods()
        findBestCard()
    }

    private fun findBestCard() {
        val best = repository.getBestCardForToday()
        if (best != null) {
            _bestCardName.value = best.name
        } else {
            _bestCardName.value = "Sem recomendação"
        }
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



    private fun findSpents() {
        val spents = repository.getSpents()
        if (spents.isNotEmpty()) {
            val result = spents.map { spent ->
                val title = spent.name

                val subtitle = if (spent.times != null && spent.times > 1) {
                    Pair(spent.value,spent.times)
                } else {
                    Pair(spent.value,0)
                }

                Triple(title, subtitle, spent)
            }

            _spentItems.value = result
        } else {
            _errorMessage.value = "Nenhum gasto encontrado"
        }
    }

}

package com.example.controledovitao.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.controledovitao.data.model.Options
import com.example.controledovitao.data.repository.SpentRepository
import java.math.BigDecimal
import java.time.LocalDate

class SpentViewModel : ViewModel() {

    private val repository = SpentRepository()

    private val _getInfosSuccess = MutableLiveData<Boolean>()
    val getInfosSuccess: LiveData<Boolean> = _getInfosSuccess

    var methods: List<Pair<String, Boolean>> = emptyList()
        private set

    private val _saveSuccess = MutableLiveData<Boolean>()
    val saveSuccess: LiveData<Boolean> = _saveSuccess

    private val _errorMessage = MutableLiveData<String>()
    val errorMessage: LiveData<String> = _errorMessage

    init {
        findMethods()
    }

    private fun findMethods() {
        val aux = repository.getMethods()

        if (aux.isNotEmpty()) {
            methods = aux.map { item ->
                val isCredit = (item.option == Options.CREDIT)
                Pair(item.name, isCredit)
            }

            _getInfosSuccess.value = true
        } else {
            _errorMessage.value = "Nenhum método de pagamento encontrado"
        }
    }

    fun saveExpense(title: String, method: String, value: BigDecimal, installments: Int, date: Long) {
        if (title.isBlank() || value <= BigDecimal("0") || installments <= 0) {
            _errorMessage.value = "Dados inválidos"
            return
        }

        try {
            val resp = repository.save(title, method, value, installments, date)
            if (resp){
                _saveSuccess.value = true
            }
            _saveSuccess.value = true

        } catch (e: Exception) {
            _errorMessage.value = "Erro ao salvar: ${e.message}"
        }
    }
}
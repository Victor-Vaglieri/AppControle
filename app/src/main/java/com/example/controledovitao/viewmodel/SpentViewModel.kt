package com.example.controledovitao.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.controledovitao.data.repository.SpentRepository
import java.math.BigDecimal

class SpentViewModel : ViewModel() {

    private val repository = SpentRepository()

    private val _saveSuccess = MutableLiveData<Boolean>()
    val saveSuccess: LiveData<Boolean> = _saveSuccess

    private val _errorMessage = MutableLiveData<String>()
    val errorMessage: LiveData<String> = _errorMessage


    fun saveExpense(title: String, method: String, value: BigDecimal, installments: Int, date: Long) {
        if (title.isBlank()) {
            _errorMessage.value = "O título não pode estar vazio"
            return
        }
        if (value <= BigDecimal.ZERO) {
            _errorMessage.value = "O valor deve ser maior que zero"
            return
        }
        if (installments <= 0) {
            _errorMessage.value = "Número de parcelas inválido"
            return
        }

        try {
            repository.saveExpense(title, method, value, installments, date) { sucesso ->
                if (sucesso) {
                    _saveSuccess.value = true
                } else {
                    _errorMessage.value = "Erro ao salvar no banco de dados. Verifique sua conexão."
                }
            }
        } catch (e: Exception) {
            _errorMessage.value = "Erro interno: ${e.message}"
        }
    }
}
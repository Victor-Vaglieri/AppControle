package com.example.controledovitao.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.controledovitao.data.repository.AuthRepository

class LoginViewModel : ViewModel() {

    private val repository = AuthRepository()

    private val _loginSuccess = MutableLiveData<Boolean>()
    val loginSuccess: LiveData<Boolean> = _loginSuccess
    private val _errorMessage = MutableLiveData<String>()
    val errorMessage: LiveData<String> = _errorMessage

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    fun doLogin(email: String, pass: String) {
        if (email.isEmpty() || pass.isEmpty()) {
            _errorMessage.value = "Preencha todos os campos"
            return
        }
        _isLoading.value = true
        repository.login(email, pass) { sucesso, mensagemErro ->
            _isLoading.value = false

            if (sucesso) {
                _loginSuccess.value = true
            } else {
                _errorMessage.value = mensagemErro ?: "Login ou senha incorretos"
            }
        }
    }
}
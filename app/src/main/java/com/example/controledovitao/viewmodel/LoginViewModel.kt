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

    fun doLogin(login: String, pass: String) {
        if (login.isEmpty() || pass.isEmpty()) {
            _errorMessage.value = "Preencha todos os campos"
            return
        }

        val isValid = repository.login(login, pass)

        if (isValid) {
            _loginSuccess.value = true

        } else {
            _errorMessage.value = "Login ou senha incorretos"
        }
    }
}
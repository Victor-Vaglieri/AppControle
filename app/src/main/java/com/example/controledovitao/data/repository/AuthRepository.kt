package com.example.controledovitao.data.repository

import com.example.controledovitao.data.model.User

class AuthRepository {
    private val fakeDatabase = listOf(
        User("admin", "1234", "Victor", "teste123@gmail.com"),
        User("convidado", "0000", "Visitante", "teste123@gmail.com")
    )

    fun login(login: String, pass: String): Boolean {
        val userEncontrado = fakeDatabase.find { it.login == login && it.pass == pass }
        return userEncontrado != null
    }
}
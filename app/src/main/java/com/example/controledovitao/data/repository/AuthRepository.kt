package com.example.controledovitao.data.repository

import com.example.controledovitao.data.model.User

class AuthRepository {

    // TODO trocar para um arquivo na nuvem
    private val fakeDatabase = listOf(
        User("admin", "1234", "Victor"),
        User("convidado", "0000", "Visitante")
    )

    fun login(login: String, pass: String): Boolean {
        val userEncontrado = fakeDatabase.find { it.login == login && it.pass == pass }
        return userEncontrado != null
    }
}
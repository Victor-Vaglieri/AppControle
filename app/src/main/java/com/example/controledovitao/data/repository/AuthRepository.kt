package com.example.controledovitao.data.repository

import com.example.controledovitao.data.model.User
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.firestore.firestore

object AuthRepository {

    private val auth: FirebaseAuth = Firebase.auth
    private val db = Firebase.firestore
    private val usersCollection = db.collection("users")

    fun login(loginInput: String, pass: String, onResult: (Boolean, String?) -> Unit) {
        if (loginInput.isBlank() || pass.isBlank()) {
            onResult(false, "Preencha login e senha")
            return
        }

        if (loginInput.contains("@")) {
            authenticateWithEmail(loginInput, pass, onResult)
        } else {
            usersCollection.whereEqualTo("name", loginInput).get()
                .addOnSuccessListener { documents ->
                    if (documents.isEmpty) {
                        onResult(false, "Usuário não encontrado")
                    } else {
                        // Pega o e-mail do primeiro usuário encontrado com esse nome
                        val email = documents.documents[0].getString("email")
                        if (email != null) {
                            authenticateWithEmail(email, pass, onResult)
                        } else {
                            onResult(false, "E-mail inválido no banco de dados")
                        }
                    }
                }
                .addOnFailureListener {
                    onResult(false, "Erro ao buscar usuário no banco")
                }
        }
    }

    private fun authenticateWithEmail(email: String, pass: String, onResult: (Boolean, String?) -> Unit) {
        auth.signInWithEmailAndPassword(email, pass)
            .addOnSuccessListener {
                onResult(true, null)
            }
            .addOnFailureListener { e ->
                onResult(false, "Login ou senha incorretos")
            }
    }

    fun createUserOnce(email: String, pass: String, name: String, onResult: (Boolean, String?) -> Unit) {
        auth.createUserWithEmailAndPassword(email, pass)
            .addOnSuccessListener { result ->
                val uid = result.user?.uid ?: ""
                val newUser = User(
                    id = uid,
                    name = name,
                    email = email
                )

                usersCollection.document(uid).set(newUser)
                    .addOnSuccessListener {
                        onResult(true, "Usuário criado! Agora faça login.")
                    }
                    .addOnFailureListener {
                        onResult(false, "Erro ao salvar dados no banco.")
                    }
            }
            .addOnFailureListener { e ->
                onResult(false, e.message)
            }
    }

    fun logout() {
        auth.signOut()
    }

    fun updateUserName(newName: String, onResult: (Boolean) -> Unit) {
        val currentUser = auth.currentUser
        if (currentUser == null) {
            onResult(false)
            return
        }
        usersCollection.document(currentUser.uid)
            .update("name", newName)
            .addOnSuccessListener {
                onResult(true)
            }
            .addOnFailureListener {
                onResult(false)
            }
    }

    fun isLogged(): Boolean {
        return auth.currentUser != null
    }
}
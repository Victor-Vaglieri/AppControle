package com.example.controledovitao.data.repository

import com.example.controledovitao.data.model.User
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.firestore.firestore

class AuthRepository {

    private val auth: FirebaseAuth = Firebase.auth
    private val db = Firebase.firestore
    private val usersCollection = db.collection("users")

    fun login(email: String, pass: String, onResult: (Boolean, String?) -> Unit) {
        if (email.isBlank() || pass.isBlank()) {
            onResult(false, "Preencha e-mail e senha")
            return
        }

        auth.signInWithEmailAndPassword(email, pass)
            .addOnSuccessListener {
                onResult(true, null)
            }
            .addOnFailureListener { e ->
                onResult(false, e.message ?: "Erro no login")
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

    fun isLogged(): Boolean {
        return auth.currentUser != null
    }
}
package com.example.controledovitao

import android.app.Application
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import com.google.firebase.firestore.firestoreSettings
import com.example.controledovitao.data.repository.AuthRepository
class MyApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        val db = Firebase.firestore
        val settings = firestoreSettings {
            isPersistenceEnabled = true
        }
        db.firestoreSettings = settings
        AuthRepository.logout()
    }
}
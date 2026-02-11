package com.example.controledovitao.data.repository

import android.util.Log
import com.example.controledovitao.data.model.Payment
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import com.google.firebase.firestore.toObject

class PaymentRepository {

    private val db = Firebase.firestore
    private val collection = db.collection("payment_methods")

    fun listenToMethods(onUpdate: (List<Payment>) -> Unit) {
        collection.addSnapshotListener { snapshot, error ->
            if (error != null) {
                Log.e("PaymentRepo", "Erro ao buscar métodos", error)
                return@addSnapshotListener
            }

            if (snapshot != null) {
                val list = snapshot.documents.mapNotNull { doc ->
                    doc.toObject<Payment>()
                }
                onUpdate(list)
            }
        }
    }

    fun saveMethod(payment: Payment, onResult: (Boolean) -> Unit) {
        collection.add(payment)
            .addOnSuccessListener { onResult(true) }
            .addOnFailureListener {
                Log.e("PaymentRepo", "Erro ao criar", it)
                onResult(false)
            }
    }

    fun updateMethod(payment: Payment, onResult: (Boolean) -> Unit) {
        if (payment.id.isEmpty()) {
            onResult(false)
            return
        }

        collection.document(payment.id).set(payment)
            .addOnSuccessListener { onResult(true) }
            .addOnFailureListener {
                Log.e("PaymentRepo", "Erro ao atualizar", it)
                onResult(false)
            }
    }

    fun deleteMethod(id: String) {
        collection.document(id).delete()
    }
}
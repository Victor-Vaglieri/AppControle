package com.example.controledovitao.data.repository

import android.util.Log
import com.example.controledovitao.data.model.Payment
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import com.google.firebase.firestore.toObject

object PaymentRepository {

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
                Log.d("PaymentRepo", "Dados recebidos: ${list.size} cartões")
                onUpdate(list)
            }
        }
    }

    fun saveMethod(payment: Payment, onResult: (Boolean) -> Unit) {
        collection.add(payment)
            .addOnSuccessListener { onResult(true) }
            .addOnFailureListener { onResult(false) }
    }

    fun updateMethod(payment: Payment, onResult: (Boolean) -> Unit) {
        if (payment.id.isEmpty()) {
            onResult(false)
            return
        }
        collection.document(payment.id).set(payment)
            .addOnSuccessListener { onResult(true) }
            .addOnFailureListener { onResult(false) }
    }

    fun deleteMethod(id: String) {
        collection.document(id).delete()
    }

    fun closeInvoice(paymentId: String, onResult: (Boolean) -> Unit) {
        if (paymentId.isEmpty()) {
            onResult(false)
            return
        }

        collection.document(paymentId)
            .update("spent", emptyList<Any>())
            .addOnSuccessListener { onResult(true) }
            .addOnFailureListener { onResult(false) }
    }
}
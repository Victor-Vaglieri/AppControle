package com.example.controledovitao.data.repository

import android.util.Log
import com.example.controledovitao.data.model.Invest
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import com.google.firebase.firestore.toObject

object InvestmentsRepository {

    private val db = Firebase.firestore
    private val collection = db.collection("investments")

    fun listenToInvestments(onUpdate: (List<Invest>) -> Unit) {
        collection.addSnapshotListener { snapshot, error ->
            if (error != null) {
                Log.e("InvestRepo", "Erro ao buscar investimentos", error)
                return@addSnapshotListener
            }

            if (snapshot != null) {
                val investments = snapshot.documents.mapNotNull { doc ->
                    doc.toObject<Invest>()
                }
                onUpdate(investments)
            }
        }
    }

    fun saveInvestment(invest: Invest, onResult: (Boolean) -> Unit) {
        collection.add(invest)
            .addOnSuccessListener { onResult(true) }
            .addOnFailureListener { onResult(false) }
    }

    fun updateInvestment(invest: Invest, onResult: (Boolean) -> Unit) {
        if (invest.id.isEmpty()) {
            onResult(false)
            return
        }

        collection.document(invest.id).set(invest)
            .addOnSuccessListener { onResult(true) }
            .addOnFailureListener { onResult(false) }
    }

    fun deleteInvestment(id: String, onResult: ((Boolean) -> Unit)? = null) {
        if (id.isEmpty()) {
            onResult?.invoke(false)
            return
        }

        collection.document(id).delete()
            .addOnSuccessListener { onResult?.invoke(true) }
            .addOnFailureListener { onResult?.invoke(false) }
    }
}
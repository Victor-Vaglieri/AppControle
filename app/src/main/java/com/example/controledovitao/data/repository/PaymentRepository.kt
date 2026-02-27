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


    fun syncBalanceForSameBank(paymentName: String, newBalance: Double) {
        val bankPrefix = paymentName.lowercase().split(" ").firstOrNull() ?: return

        collection.get().addOnSuccessListener { snapshot ->
            val batch = db.batch()

            for (doc in snapshot.documents) {
                val currentName = doc.getString("name") ?: ""

                if (currentName.startsWith(bankPrefix, ignoreCase = true)) {
                    batch.update(doc.reference, "balance", newBalance)
                }
            }

            batch.commit()
                .addOnSuccessListener { Log.d("Sync", "Saldos sincronizados com sucesso!") }
                .addOnFailureListener { Log.e("Sync", "Erro ao sincronizar saldos", it) }
        }
    }

    fun deleteMethod(id: String) {
        collection.document(id).delete()
    }

    fun closeInvoice(paymentId: String, onResult: (Boolean) -> Unit) {
        if (paymentId.isEmpty()) {
            onResult(false)
            return
        }
        val docRef = collection.document(paymentId)
        docRef.get().addOnSuccessListener { snapshot ->
            val payment = snapshot.toObject<Payment>()

            if (payment != null) {
                var valorFaturaAtual = 0.0
                val remainingSpents = payment.spent.mapNotNull { spent ->
                    val valorParcela = spent.value / spent.times
                    valorFaturaAtual += valorParcela

                    if (spent.times > 1) {
                        spent.copy(
                            times = spent.times - 1,
                            value = spent.value - valorParcela
                        )
                    } else {
                        null
                    }
                }

                val currentUsage = payment.usage ?: 0.0
                val newUsage = (currentUsage - valorFaturaAtual).coerceAtLeast(0.0)

                val newBalance = payment.balance - valorFaturaAtual

                docRef.update(
                    mapOf(
                        "spent" to remainingSpents,
                        "usage" to newUsage,
                        "balance" to newBalance
                    )
                ).addOnSuccessListener {
                    syncBalanceForSameBank(payment.name, newBalance)

                    onResult(true)
                }.addOnFailureListener {
                    onResult(false)
                }

            } else {
                onResult(false)
            }
        }.addOnFailureListener {
            onResult(false)
        }
    }
}
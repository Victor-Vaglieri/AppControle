package com.example.controledovitao.data.repository

import android.util.Log
import com.example.controledovitao.data.model.Options
import com.example.controledovitao.data.model.Payment
import com.example.controledovitao.data.model.Spent
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import com.google.firebase.firestore.toObject
import java.math.BigDecimal

class SpentRepository {

    private val db = Firebase.firestore
    private val collection = db.collection("payment_methods")

    fun saveExpense(
        title: String,
        methodName: String,
        value: BigDecimal,
        installments: Int,
        date: Long,
        onResult: (Boolean) -> Unit
    ) {
        collection.whereEqualTo("name", methodName)
            .get()
            .addOnSuccessListener { documents ->
                if (documents.isEmpty) {
                    Log.e("SpentRepo", "Cartão não encontrado: $methodName")
                    onResult(false)
                    return@addOnSuccessListener
                }

                val document = documents.documents.first()
                val payment = document.toObject<Payment>()

                if (payment != null) {
                    val newSpent = Spent(
                        name = title,
                        value = value.toDouble(),
                        times = installments,
                        spentDate = date
                    )

                    payment.spent.add(newSpent)

                    if (payment.option == Options.CREDIT) {
                        payment.balance = payment.balance - newSpent.value
                        val usoAtual = payment.usage ?: 0.0
                        payment.usage = usoAtual + newSpent.value
                    } else if (payment.option == Options.DEBIT || payment.option == Options.MONEY) {
                        payment.balance = payment.balance - newSpent.value
                    }

                    collection.document(document.id).set(payment)
                        .addOnSuccessListener {
                            Log.d("SpentRepo", "Gasto salvo e saldo atualizado!")
                            onResult(true)
                        }
                        .addOnFailureListener { e ->
                            Log.e("SpentRepo", "Erro ao atualizar cartão", e)
                            onResult(false)
                        }

                } else {
                    onResult(false)
                }
            }
            .addOnFailureListener { e ->
                Log.e("SpentRepo", "Erro ao buscar cartão", e)
                onResult(false)
            }
    }
}
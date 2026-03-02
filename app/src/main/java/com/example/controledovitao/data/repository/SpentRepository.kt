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
                    val doubleValue = value.toDouble()

                    val newSpent = Spent(
                        name = title,
                        value = doubleValue,
                        times = installments,
                        spentDate = date
                    )

                    val updatedSpents = payment.spent.toMutableList()
                    updatedSpents.add(newSpent)
                    payment.spent = updatedSpents

                    // --- CORREÇÃO AQUI ---
                    if (payment.option == Options.CREDIT) {
                        // Crédito: Apenas aumenta o limite usado (fatura). NÃO mexe no saldo da conta.
                        val usoAtual = payment.usage ?: 0.0
                        payment.usage = usoAtual + doubleValue
                    } else {
                        // Débito/Dinheiro: Debita o valor direto do saldo em conta.
                        payment.balance = (payment.balance - doubleValue)
                    }

                    collection.document(document.id).set(payment)
                        .addOnSuccessListener {
                            Log.d("SpentRepo", "Gasto salvo e saldo/fatura atualizado!")

                            // Se foi um gasto no Débito ou Dinheiro, sincroniza o novo saldo
                            // com a conta de Crédito (ex: MercadoPago Débito avisa o MercadoPago Crédito)
                            if (payment.option != Options.CREDIT) {
                                PaymentRepository.syncBalanceForSameBank(payment.name, payment.balance)
                            }

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
                Log.e("SpentRepo", "Erro ao buscar cartão para salvar gasto", e)
                onResult(false)
            }
    }
}
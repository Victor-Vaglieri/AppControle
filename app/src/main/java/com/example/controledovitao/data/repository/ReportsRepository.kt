package com.example.controledovitao.data.repository

import android.util.Log
import com.example.controledovitao.data.model.Payment
import com.example.controledovitao.ui.adapter.ChartData
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import com.google.firebase.firestore.toObject
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class ReportsRepository {

    private val db = Firebase.firestore
    private val collection = db.collection("payment_methods")

    fun listenToChartsData(onUpdate: (List<ChartData>) -> Unit) {
        collection.addSnapshotListener { snapshot, error ->
            if (error != null) {
                Log.e("ReportsRepo", "Erro ao buscar dados", error)
                return@addSnapshotListener
            }

            if (snapshot != null) {
                val payments = snapshot.documents.mapNotNull { doc ->
                    doc.toObject<Payment>()
                }
                val allSpents = payments.flatMap { it.spent }

                val sortedSpents = allSpents.sortedByDescending { it.spentDate }
                val groupedByMonth = sortedSpents.groupBy { spent ->
                    val date = Date(spent.spentDate)
                    val formatter = SimpleDateFormat("MMMM", Locale("pt", "BR"))
                    formatter.format(date).replaceFirstChar { it.uppercase() }
                }

                val chartDataList = groupedByMonth.map { (monthName, spentsList) ->
                    val values = spentsList.map { it.value.toFloat() }
                    ChartData(monthName, values)
                }
                onUpdate(chartDataList)
            }
        }
    }
}
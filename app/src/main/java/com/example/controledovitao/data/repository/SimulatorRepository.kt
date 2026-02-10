package com.example.controledovitao.data.repository

import android.util.Log
import com.example.controledovitao.data.model.SimulationOption
import com.example.controledovitao.data.model.SimulationType
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import com.google.firebase.firestore.toObject

class SimulationRepository {

    private val db = Firebase.firestore
    private val collection = db.collection("simulation_options")

    fun getOptions(type: SimulationType, onResult: (List<SimulationOption>) -> Unit) {
        collection.whereEqualTo("type", type.name)
            .get()
            .addOnSuccessListener { result ->
                if (result != null) {
                    val list = result.documents.mapNotNull { doc ->
                        doc.toObject<SimulationOption>()
                    }
                    onResult(list)
                } else {
                    onResult(emptyList())
                }
            }
            .addOnFailureListener { exception ->
                Log.e("SimRepo", "Erro ao buscar opções", exception)
                onResult(emptyList())
            }
    }


    fun seedInitialData() {
        val list = listOf(
            SimulationOption("1", "Poupança", SimulationType.BANCO.name, 0.0617),
            SimulationOption("2", "CDB (100% CDI)", SimulationType.BANCO.name, 0.105),
            SimulationOption("3", "Tesouro Direto", SimulationType.BANCO.name, 0.11),
            SimulationOption("4", "Bitcoin (BTC)", SimulationType.CRIPTO.name, 0.60),
            SimulationOption("5", "Ethereum (ETH)", SimulationType.CRIPTO.name, 0.45),
            SimulationOption("6", "Solana (SOL)", SimulationType.CRIPTO.name, 0.80)
        )
        list.forEach { collection.add(it) }
    }
}
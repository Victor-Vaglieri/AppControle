package com.example.controledovitao.data.model

import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.Exclude

enum class SimulationType {
    BANCO,
    CRIPTO;

    companion object {
        fun safeValueOf(value: String): SimulationType {
            return try {
                valueOf(value)
            } catch (e: Exception) {
                BANCO
            }
        }
    }
}

data class SimulationOption(
    @DocumentId
    val id: String = "",
    val name: String = "",
    val typeString: String = SimulationType.BANCO.name,
    val annualRate: Double = 0.0
) {
    @get:Exclude
    val type: SimulationType
        get() = SimulationType.safeValueOf(typeString)
}
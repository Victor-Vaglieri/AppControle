package com.example.controledovitao.data.model

import com.example.controledovitao.data.repository.SimulationType

data class SimulationOption(
    val id: String,
    val name: String,
    val type: SimulationType,
    val annualRate: Double
)
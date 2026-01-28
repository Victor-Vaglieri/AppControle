package com.example.controledovitao.data.repository

import com.example.controledovitao.ui.adapter.ChartData

class ReportsRepository {

    fun getCharts(): List<ChartData> {
        // Simulação de dados (Mock)
        return listOf(
            ChartData("Dezembro", listOf(200f, 450f, 300f, 800f, 150f)),
            ChartData("Novembro", listOf(600f, 200f, 500f, 300f, 400f))
        )
    }
}
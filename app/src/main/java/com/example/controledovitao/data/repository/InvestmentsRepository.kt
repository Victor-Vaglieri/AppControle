package com.example.controledovitao.data.repository

import com.example.controledovitao.data.model.Invest
import java.math.BigDecimal
import java.time.Period

class InvestmentsRepository {

    fun getInvestmentsList(): List<Invest> {
        return listOf(
            Invest(
                name = "Poupança",
                value = BigDecimal("1500.00"),
                spentDate = System.currentTimeMillis(),
                period = Period.of(4, 4, 0),
                estimate = BigDecimal("2078.78")
            ),
            Invest(
                name = "BitCoin (BC)",
                value = BigDecimal("1500.00"),
                spentDate = System.currentTimeMillis(),
                period = Period.of(1, 0, 0),
                estimate = BigDecimal("1920.00")
            )
        )
    }
}
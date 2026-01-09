package com.example.controledovitao.data.repository

import com.example.controledovitao.data.model.Options
import com.example.controledovitao.data.model.Payment
import com.example.controledovitao.data.model.Spent
import java.math.BigDecimal
import java.time.LocalDate

class SpentRepository {

    private val fakePayment = listOf(
        Payment(
            "Visa Crédito",
            Options.CREDIT,
            BigDecimal("3000"),
            BigDecimal("2000"),
            BigDecimal("1000"),
            23,
            1,
            mutableListOf(
                Spent(
                    name = "Almoço",
                    value = BigDecimal("35.00"),
                    times = 2,
                    spentDate = System.currentTimeMillis()
                ),
                Spent(
                    name = "Uber",
                    value = BigDecimal("18.50"),
                    times = 1,
                    spentDate = System.currentTimeMillis() - 86400000
                )
            )
        ),
        Payment(
            "Visa Débito",
            Options.DEBIT,
            BigDecimal("3000"),
            null,
            null,
            null,
            null,
            mutableListOf()
        ),
        Payment(
            "Master Crédito",
            Options.CREDIT,
            BigDecimal("1500"),
            BigDecimal("500"),
            BigDecimal.ZERO,
            8,
            15,
            mutableListOf()
        ),
        Payment(
            "Master Débito",
            Options.DEBIT,
            BigDecimal("10.54"),
            null,
            null,
            null,
            null,
            mutableListOf()
        ),
        Payment(
            "PIX",
            Options.MONEY,
            BigDecimal("23.40"),
            null,
            null,
            null,
            null,
            mutableListOf()
        )
    )

    fun getMethods(): List<Payment> {
        return fakePayment
    }

    fun save(title: String, method: String, value: BigDecimal, installments: Int, date: Long): Boolean {

        // 1. Cria o objeto Spent novo
        val newSpent = Spent(
            name = title,
            value = value,
            times = installments,
            spentDate = date
        )

        val paymentFound = fakePayment.find { it.name == method }

        if (paymentFound != null) {
            paymentFound.spent.add(newSpent)
            println("Gasto salvo no FAKE DB: $newSpent em $method")
            return true
        }
        return false
    }
}
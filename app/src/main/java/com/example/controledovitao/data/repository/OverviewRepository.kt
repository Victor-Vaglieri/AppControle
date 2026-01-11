package com.example.controledovitao.data.repository

import com.example.controledovitao.data.model.Overview
import com.example.controledovitao.data.model.Payment
import com.example.controledovitao.data.model.Invest
import com.example.controledovitao.data.model.Options
import com.example.controledovitao.data.model.Spent
import java.math.BigDecimal
import java.time.LocalDate
import java.time.Period

class OverviewRepository {

    // TODO puxar da nuvem e deixar na memoria
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

    // TODO puxar da nuvem e deixar na memoria (talvez)
    private val fakeInvest = listOf(
        Invest(
            "Poupança",
            BigDecimal("1000"),
            Period.between(
                LocalDate.of(2025, 1, 1),
                LocalDate.of(2026, 1, 1)
            ),
            BigDecimal("100")
        ),
        Invest(
            "BitCoin",
            BigDecimal("1000"),
            Period.between(
                LocalDate.of(2025, 1, 1),
                LocalDate.of(2026, 1, 1)
            ),
            BigDecimal("280")
        )
    )

    fun getBalance(): List<BigDecimal> {

        val totalBalance = fakePayment.fold(BigDecimal.ZERO) { acc, payment ->
            acc.add(payment.balance)
        }

        val totalLimit = fakePayment.fold(BigDecimal.ZERO) { acc, payment ->
            acc.add(payment.limit ?: BigDecimal.ZERO)
        }

        val totalUsage = fakePayment.fold(BigDecimal.ZERO) { acc, payment ->
            acc.add(payment.usage ?: BigDecimal.ZERO)
        }

        val totalInvest = fakeInvest.fold(BigDecimal.ZERO) { acc, invest ->
            acc.add(invest.value)
        }

        return listOf(
            totalInvest,
            totalBalance,
            totalLimit,
            totalUsage
        )
    }
    fun getSpents(): List<Spent> =
        fakePayment.flatMap { it.spent ?: emptyList() }
}

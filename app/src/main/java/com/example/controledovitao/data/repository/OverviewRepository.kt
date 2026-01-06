package com.example.controledovitao.data.repository

import com.example.controledovitao.data.model.Overview
import com.example.controledovitao.data.model.Payment
import com.example.controledovitao.data.model.Invest
import com.example.controledovitao.data.model.Options
import java.math.BigDecimal

class OverviewRepository {

    // TODO puxar da nuvem e deixar na memoria
    private val fakePayment = listOf(
        Payment("Visa Crédito", Options.CREDIT,  BigDecimal(3000),  BigDecimal(2000),BigDecimal(1000),23, 1),
        Payment("Visa Débito", Options.DEBIT,  BigDecimal(3000),  null,  null, null, null),
        Payment("Master Crédito", Options.CREDIT,  BigDecimal(1500),  BigDecimal(500), BigDecimal(0),8,  15),
        Payment("Master Débito", Options.DEBIT,  BigDecimal(10.54),  null,  null, null, null),
        Payment("PIX", Options.MONEY,  BigDecimal(23.40),  null,  null, null, null)
    )

    // TODO deixar na memoria, ao inicializar coletar as infos e toda mudança atualizar
    private val fakeOverview = Overview(BigDecimal(0),BigDecimal(0),BigDecimal(0),emptyList(),emptyList())


    // TODO puxar da nuvem e deixar na memoria (talvez)
    // TODO criar fake invest

    fun getOverview(): List<BigDecimal> {

        val balance = fakePayment.sumOf { it.balance }
        fakeOverview.totalBalance = balance
        val limit = fakePayment.sumOf { it.limit ?: BigDecimal(0.0)}
        fakeOverview.totalLimit = limit

        val usage = fakePayment.sumOf { it.usage ?: BigDecimal(0.0)}

        val invest = BigDecimal(0.0)
        fakeOverview.totalInvest = invest

        return mutableListOf(fakeOverview.totalBalance,fakeOverview.totalLimit,usage)
    }
}
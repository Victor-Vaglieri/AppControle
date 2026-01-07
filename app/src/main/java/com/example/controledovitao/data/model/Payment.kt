package com.example.controledovitao.data.model

import java.math.BigDecimal

data class Payment(
    val name: String,
    val option: Options,
    var balance: BigDecimal,
    var limit: BigDecimal?,
    var usage: BigDecimal?,
    var bestDate: Int?,
    var shutdown: Int?,
    var spent: List<Spent>
)

enum class Options(val op: String) {
    CREDIT("credit"),
    DEBIT("debit"),
    MONEY("money")
}

data class Spent(
    val name: String,
    val value: BigDecimal,
    val times: Int?
)
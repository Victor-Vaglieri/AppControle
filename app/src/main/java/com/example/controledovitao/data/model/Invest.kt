package com.example.controledovitao.data.model

import java.math.BigDecimal
import java.time.Period

data class Invest (
    val name: String,
    var value: BigDecimal,
    val spentDate: Long,
    var period: Period,
    var estimate: BigDecimal,
)
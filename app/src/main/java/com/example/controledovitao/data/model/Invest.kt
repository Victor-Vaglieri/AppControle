package com.example.controledovitao.data.model

import java.math.BigDecimal
import java.time.Period

data class Invest (
    val name: String,
    var value: BigDecimal,
    var period: Period,
    var estimate: BigDecimal,
)
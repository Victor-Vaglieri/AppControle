package com.example.controledovitao.data.model

import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.Exclude
import java.math.BigDecimal
import java.time.Period

data class Invest(
    @DocumentId
    val id: String = "",
    val name: String = "",
    val value: Double = 0.0,
    val spentDate: Long = 0L,
    val period: String = "",

    val estimate: Double = 0.0
) {

    @get:Exclude
    val valueAsBigDecimal: BigDecimal
        get() = BigDecimal.valueOf(value)

    @get:Exclude
    val estimateAsBigDecimal: BigDecimal
        get() = BigDecimal.valueOf(estimate)

    @get:Exclude
    val periodAsObject: Period?
        get() = if (period.isNotEmpty()) Period.parse(period) else null
}
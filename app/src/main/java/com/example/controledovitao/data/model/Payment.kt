package com.example.controledovitao.data.model

import java.math.BigDecimal
import android.os.Parcelable
import kotlinx.parcelize.Parcelize
data class Payment(
    val name: String,
    val option: Options,
    var balance: BigDecimal,
    var limit: BigDecimal?,
    var usage: BigDecimal?,
    var bestDate: Int?,
    var shutdown: Int?,
    var spent: MutableList<Spent>
)

enum class Options(val op: String) {
    CREDIT("credit"),
    DEBIT("debit"),
    MONEY("money")
}

@Parcelize
data class Spent(
    val name: String,
    val value: BigDecimal,
    val times: Int = 1,
    val spentDate: Long
) : Parcelable {
    val formattedDate: String
        get() {
            val formatter = java.text.SimpleDateFormat("dd/MM/yyyy", java.util.Locale("pt", "BR"))
            return formatter.format(java.util.Date(spentDate))
        }
}
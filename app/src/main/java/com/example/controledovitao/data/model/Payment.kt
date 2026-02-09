package com.example.controledovitao.data.model

import java.math.BigDecimal
import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import kotlinx.parcelize.IgnoredOnParcel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.Exclude
data class Payment(
    @DocumentId
    val id: String = "",
    val name: String = "",
    val optionType: String = Options.CREDIT.op,
    val balance: Double = 0.0,
    val limit: Double? = null,
    val usage: Double? = null,
    val bestDate: Int? = null,
    val shutdown: Int? = null,

    var spent: MutableList<Spent> = mutableListOf()
) {
    @get:Exclude
    val option: Options
        get() = Options.getByOp(optionType)

    @get:Exclude
    val balanceAsBigDecimal: BigDecimal
        get() = BigDecimal.valueOf(balance)

    @get:Exclude
    val limitAsBigDecimal: BigDecimal?
        get() = limit?.let { BigDecimal.valueOf(it) }

    @get:Exclude
    val usageAsBigDecimal: BigDecimal?
        get() = usage?.let { BigDecimal.valueOf(it) }
}
enum class Options(val op: String) {
    CREDIT("credit"),
    DEBIT("debit"),
    MONEY("money");

    companion object {
        fun getByOp(value: String): Options {
            return entries.find { it.op == value } ?: MONEY
        }
    }
}

@Parcelize
data class Spent(
    val name: String = "",

    // Firebase salva Double.
    val value: Double = 0.0,

    val times: Int = 1,
    val spentDate: Long = 0L
) : Parcelable {

    @get:Exclude
    @IgnoredOnParcel
    val valueAsBigDecimal: BigDecimal
        get() = BigDecimal.valueOf(value)

    @get:Exclude
    val formattedDate: String
        get() {
            val formatter = SimpleDateFormat("dd/MM/yyyy", Locale("pt", "BR"))
            return formatter.format(Date(spentDate))
        }
}
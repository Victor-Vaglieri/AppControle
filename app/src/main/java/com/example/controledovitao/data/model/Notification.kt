package com.example.controledovitao.data.model
import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.Exclude

data class Notification(
    @DocumentId
    val id: String = "",
    val statusOp: Int = 0,
    val title: String = "",
    val description: String = "",
    val createDate: Long = 0L,
    val expiryDate: Long = 0L,
    @get:Exclude
    var isExpanded: Boolean = false
) {
    @get:Exclude
    val status: Status
        get() = Status.getByOp(statusOp)
}
enum class Status(val op: Int) {
    URGENT(2),
    STANDARD(1),
    INFO(0),
    CONCLUDE(-1);

    companion object {
        fun getByOp(value: Int): Status {
            return entries.find { it.op == value } ?: INFO
        }
    }
}
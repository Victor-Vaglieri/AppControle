package com.example.controledovitao.data.model

data class Notification (
    val status: Status,
    val title: String,
    val description: String,
    val createDate: Long,
    val expiryDate: Long,
    var isExpanded: Boolean = false
)

enum class Status(val op: Int) {
    URGENT(2),
    STANDARD(1),
    INFO(0),
    CONCLUDE(-1)
}
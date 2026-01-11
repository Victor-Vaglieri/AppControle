package com.example.controledovitao.viewmodel

import androidx.lifecycle.ViewModel
import com.example.controledovitao.data.repository.NotificationsRepository

class NotificationsViewModel : ViewModel(){
    private val repository = NotificationsRepository()

    var listNotifications: List<Triple<Int, String, String>> = emptyList()
        private set

    init {
        loadNotifications()
    }

    private fun loadNotifications(){
        val aux = repository.getNotifications().map {item ->
            val itemStatus = item.status.op
            Triple(itemStatus, item.title,item.description)}
        listNotifications = aux
    }

    fun updatePush(option: Boolean) {
        repository.savePushPreference(option)
    }

    fun updateEmail(option: Boolean) {
        repository.saveEmailPreference(option)
    }
}
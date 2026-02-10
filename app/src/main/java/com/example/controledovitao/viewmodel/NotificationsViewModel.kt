package com.example.controledovitao.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.example.controledovitao.data.model.Notification
import com.example.controledovitao.data.model.Status
import com.example.controledovitao.data.repository.NotificationsRepository
import kotlinx.coroutines.launch

class NotificationsViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = NotificationsRepository(application)

    private val _notifications = MutableLiveData<List<Notification>>()
    val notifications: LiveData<List<Notification>> = _notifications

    val isPushEnabled: LiveData<Boolean> = repository.isPushEnabled.asLiveData()
    val isEmailEnabled: LiveData<Boolean> = repository.isEmailEnabled.asLiveData()

    init {
        loadNotifications()
    }

    private fun loadNotifications() {
        repository.listenToNotifications { list ->
            _notifications.value = list
        }
    }

    fun updatePush(isEnabled: Boolean) {
        viewModelScope.launch {
            repository.savePushPreference(isEnabled)
        }
    }

    fun updateEmail(isEnabled: Boolean) {
        viewModelScope.launch {
            repository.saveEmailPreference(isEnabled)
        }
    }

}
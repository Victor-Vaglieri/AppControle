package com.example.controledovitao.viewmodel

import android.app.Application
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.example.controledovitao.data.repository.ConfigRepository
import kotlinx.coroutines.launch

class ConfigViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = ConfigRepository(application)
    private val _userName = MutableLiveData<String>()
    val userName: LiveData<String> = _userName

    private val _userPhoto = MutableLiveData<Uri?>()
    val userPhoto: LiveData<Uri?> = _userPhoto

    val isThemeDark = repository.isThemeDark.asLiveData()
    val isBackupEnabled = repository.isBackupEnabled.asLiveData()
    val isBiometricEnabled = repository.isBiometricEnabled.asLiveData()
    val isDataCollectionEnabled = repository.isDataCollectionEnabled.asLiveData()

    init {
        loadUserData()
    }

    private fun loadUserData() {
        _userPhoto.value = repository.getProfileImage()
        viewModelScope.launch {
            _userName.value = repository.getUserName()
        }
    }


    fun toggleTheme(enabled: Boolean) {
        viewModelScope.launch {
            repository.saveThemePreference(enabled)
        }
    }

    fun toggleBackup(enabled: Boolean) {
        viewModelScope.launch { repository.saveBackupPreference(enabled) }
    }

    fun toggleBiometric(enabled: Boolean) {
        viewModelScope.launch { repository.saveBiometricPreference(enabled) }
    }

    fun toggleDataCollection(enabled: Boolean) {
        viewModelScope.launch { repository.saveDataCollectionPreference(enabled) }
    }
}
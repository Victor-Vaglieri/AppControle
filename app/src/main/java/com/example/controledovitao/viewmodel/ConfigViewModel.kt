package com.example.controledovitao.viewmodel

import android.net.Uri
import androidx.lifecycle.ViewModel
import com.example.controledovitao.data.repository.ConfigRepository

class ConfigViewModel : ViewModel() {
    private val repository = ConfigRepository()

    var userName: String = ""
        private set

    var image: Uri? = null
        private set

    init {
        loadUserData()
    }

    private fun loadUserData() {
        userName = repository.getUserName()
        image = repository.getProfileImage()
    }


    fun updateTheme(option: Boolean) {
        repository.saveThemePreference(option)
    }

    fun updateBackup(option: Boolean) {
        repository.saveBackupPreference(option)
    }

    fun updateBiometria(option: Boolean) {
        repository.saveBiometricPreference(option)
    }

    fun updateColeta(option: Boolean) {
        repository.saveDataCollectionPreference(option)
    }
}
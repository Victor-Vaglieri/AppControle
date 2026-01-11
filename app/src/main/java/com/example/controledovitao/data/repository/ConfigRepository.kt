package com.example.controledovitao.data.repository

import android.net.Uri

class ConfigRepository {

    fun getUserName(): String {
        return "Victor Vaglieri de Oliveira" // Simulado
        // return sharedPreferences.getString("user_name", "")
    }

    fun getProfileImage(): Uri? {
        return null
        // return Uri.parse(sharedPreferences.getString("profile_uri", ""))
    }


    fun saveThemePreference(isEnabled: Boolean) {
        // TODO: Implementar salvamento real (SharedPreferences ou DataStore)
        println("Configuração salva: Tema Escuro = $isEnabled")
    }

    fun saveBackupPreference(isEnabled: Boolean) {
        println("Configuração salva: Backup = $isEnabled")
    }

    fun saveBiometricPreference(isEnabled: Boolean) {
        println("Configuração salva: Biometria = $isEnabled")
    }

    fun saveDataCollectionPreference(isEnabled: Boolean) {
        println("Configuração salva: Coleta de Dados = $isEnabled")
    }
}
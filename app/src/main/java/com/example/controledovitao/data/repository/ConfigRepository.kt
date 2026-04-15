package com.example.controledovitao.data.repository

import android.content.Context
import android.net.Uri
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.flow.first

private val Context.settingsDataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

class ConfigRepository(
    private val context: Context,
    private val auth: FirebaseAuth = FirebaseAuth.getInstance(),
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance(),
    private val dataStore: DataStore<Preferences> = context.settingsDataStore
) {

    companion object {
        val THEME_KEY = booleanPreferencesKey("theme_dark")
        val BACKUP_KEY = booleanPreferencesKey("backup_auto")
        val BIOMETRIC_KEY = booleanPreferencesKey("biometric_login")
        val DATA_KEY = booleanPreferencesKey("data_collection")
    }

    suspend fun getUserName(): String {
        val user = auth.currentUser ?: return "Usuário Deslogado"
        if (!user.displayName.isNullOrBlank()) {
            return user.displayName!!
        }
        return try {
            val doc = db.collection("users").document(user.uid).get().await()
            doc.getString("name") ?: user.email ?: "Usuário"
        } catch (e: Exception) {
            "Usuário Offline"
        }
    }

    suspend fun getBiometricEnabled(): Boolean {
        val preferences = dataStore.data.first()
        return preferences[BIOMETRIC_KEY] ?: false
    }
    fun getProfileImage(): Uri? {
        return auth.currentUser?.photoUrl
    }

    fun getUserEmail(): String {
        return auth.currentUser?.email ?: ""
    }

    val isThemeDark: Flow<Boolean> = dataStore.data
        .map { preferences -> preferences[THEME_KEY] ?: false }

    suspend fun saveThemePreference(isEnabled: Boolean) {
        dataStore.edit { preferences ->
            preferences[THEME_KEY] = isEnabled
        }
    }

    val isBackupEnabled: Flow<Boolean> = dataStore.data
        .map { preferences -> preferences[BACKUP_KEY] ?: true }

    suspend fun saveBackupPreference(isEnabled: Boolean) {
        dataStore.edit { preferences ->
            preferences[BACKUP_KEY] = isEnabled
        }
    }

    val isBiometricEnabled: Flow<Boolean> = dataStore.data
        .map { preferences -> preferences[BIOMETRIC_KEY] ?: false }

    suspend fun saveBiometricPreference(isEnabled: Boolean) {
        dataStore.edit { preferences ->
            preferences[BIOMETRIC_KEY] = isEnabled
        }
    }
    val isDataCollectionEnabled: Flow<Boolean> = dataStore.data
        .map { preferences -> preferences[DATA_KEY] ?: true }

    suspend fun saveDataCollectionPreference(isEnabled: Boolean) {
        dataStore.edit { preferences ->
            preferences[DATA_KEY] = isEnabled
        }
    }
}
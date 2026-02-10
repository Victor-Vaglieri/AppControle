package com.example.controledovitao.data.repository

import android.content.Context
import android.util.Log
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import com.example.controledovitao.data.model.Notification
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.firestore
import com.google.firebase.firestore.toObject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore by preferencesDataStore(name = "notification_prefs")

class NotificationsRepository(private val context: Context) {

    private val db = Firebase.firestore
    private val auth = Firebase.auth

    private val collectionPath = if (auth.currentUser != null) {
        "users/${auth.currentUser!!.uid}/notifications"
    } else {
        "notifications_public"
    }

    private val collection = db.collection(collectionPath)

    companion object {
        val PUSH_KEY = booleanPreferencesKey("push_enabled")
        val EMAIL_KEY = booleanPreferencesKey("email_enabled")
    }


    fun listenToNotifications(onUpdate: (List<Notification>) -> Unit) {
        collection.orderBy("createDate", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    Log.e("NotifRepo", "Erro ao buscar notificações", error)
                    return@addSnapshotListener
                }

                if (snapshot != null) {
                    val list = snapshot.documents.mapNotNull { doc ->
                        doc.toObject<Notification>()
                    }
                    onUpdate(list)
                }
            }
    }

    fun createTestNotification(notification: Notification) {
        collection.add(notification)
    }

    fun deleteNotification(id: String) {
        collection.document(id).delete()
    }

    val isPushEnabled: Flow<Boolean> = context.dataStore.data
        .map { prefs -> prefs[PUSH_KEY] ?: true } // Padrão ativado

    suspend fun savePushPreference(isEnabled: Boolean) {
        context.dataStore.edit { prefs ->
            prefs[PUSH_KEY] = isEnabled
        }
    }

    val isEmailEnabled: Flow<Boolean> = context.dataStore.data
        .map { prefs -> prefs[EMAIL_KEY] ?: true }

    suspend fun saveEmailPreference(isEnabled: Boolean) {
        context.dataStore.edit { prefs ->
            prefs[EMAIL_KEY] = isEnabled
        }
    }
}
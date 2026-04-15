package com.example.controledovitao.data.repository

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.auth
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.firestore
import io.mockk.*
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class NotificationsRepositoryTest {

    private val context: Context = mockk(relaxed = true)
    private val dataStore: DataStore<Preferences> = mockk(relaxed = true)
    private val auth: FirebaseAuth = mockk(relaxed = true)
    private val db: FirebaseFirestore = mockk(relaxed = true)
    private val collection: CollectionReference = mockk(relaxed = true)
    private lateinit var repository: NotificationsRepository

    @Before
    fun setup() {
        mockkStatic(Firebase::class)
        mockkStatic(FirebaseAuth::class)
        mockkStatic(FirebaseFirestore::class)
        mockkStatic("androidx.datastore.preferences.core.PreferencesKt")
        
        every { Firebase.auth } returns auth
        every { Firebase.firestore } returns db
        every { db.collection(any()) } returns collection
        
        // Mock do DataStore
        every { dataStore.data } returns flowOf(mockk(relaxed = true))
        coEvery { dataStore.edit(any()) } returns mockk()
        val user: FirebaseUser = mockk()
        every { auth.currentUser } returns user
        every { user.uid } returns "user123"

        repository = NotificationsRepository(context, db, auth, dataStore)
    }

    @After
    fun tearDown() {
        unmockkAll()
    }

    @Test
    fun `savePushPreference deve editar o DataStore corretamente`() = runTest {
        repository.savePushPreference(true)
        coVerify { dataStore.edit(any()) }
    }

    @Test
    fun `saveEmailPreference deve editar o DataStore corretamente`() = runTest {
        repository.saveEmailPreference(false)
        coVerify { dataStore.edit(any()) }
    }

    @Test
    fun `deleteNotification deve chamar delete no documento correto`() {
        val id = "notif_id"
        repository.deleteNotification(id)
        verify { collection.document(id).delete() }
    }
}

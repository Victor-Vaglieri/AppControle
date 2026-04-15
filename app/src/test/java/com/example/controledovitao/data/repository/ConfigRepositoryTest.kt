package com.example.controledovitao.data.repository

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import io.mockk.*
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class ConfigRepositoryTest {

    private val context: Context = mockk(relaxed = true)
    private val dataStore: DataStore<Preferences> = mockk(relaxed = true)
    private val auth: FirebaseAuth = mockk(relaxed = true)
    private val db: FirebaseFirestore = mockk(relaxed = true)
    private lateinit var repository: ConfigRepository

    @Before
    fun setup() {
        mockkStatic(FirebaseAuth::class)
        mockkStatic(FirebaseFirestore::class)
        // Mock do DataStore edit extension
        mockkStatic("androidx.datastore.preferences.core.PreferencesKt")
        
        every { FirebaseAuth.getInstance() } returns auth
        every { FirebaseFirestore.getInstance() } returns db
        
        // Mock do DataStore
        every { dataStore.data } returns flowOf(mockk(relaxed = true))
        coEvery { dataStore.edit(any()) } returns mockk()

        repository = ConfigRepository(context)
    }

    @After
    fun tearDown() {
        unmockkAll()
    }

    @Test
    fun `getUserName deve retornar nome do Firebase Auth se disponivel`() = runTest {
        val user: FirebaseUser = mockk()
        every { auth.currentUser } returns user
        every { user.displayName } returns "Victor"

        val name = repository.getUserName()
        assertEquals("Victor", name)
    }

    @Test
    fun `getUserName deve buscar no Firestore se displayName for nulo`() = runTest {
        val user: FirebaseUser = mockk()
        val docRef: DocumentReference = mockk()
        val task: Task<DocumentSnapshot> = mockk()
        val snapshot: DocumentSnapshot = mockk()

        every { auth.currentUser } returns user
        every { user.displayName } returns null
        every { user.uid } returns "123"
        every { db.collection("users").document("123") } returns docRef
        
        coEvery { docRef.get().isComplete } returns true
        coEvery { docRef.get().result } returns snapshot
        every { snapshot.getString("name") } returns "Nome Firestore"
    }

    @Test
    fun `getUserEmail deve retornar email do usuario logado`() {
        val user: FirebaseUser = mockk()
        every { auth.currentUser } returns user
        every { user.email } returns "teste@email.com"

        assertEquals("teste@email.com", repository.getUserEmail())
    }
}

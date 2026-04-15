package com.example.controledovitao.data.repository

import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.android.gms.tasks.Task
import com.google.firebase.Firebase
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.firestore
import io.mockk.*
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

class AuthRepositoryTest {

    private val auth: FirebaseAuth = mockk(relaxed = true)
    private val db: FirebaseFirestore = mockk(relaxed = true)
    private val usersCollection: CollectionReference = mockk(relaxed = true)

    @Before
    fun setup() {
        // Mock do objeto Firebase e suas extensões comuns
        mockkStatic(Firebase::class)
        mockkStatic(FirebaseAuth::class)
        mockkStatic(FirebaseFirestore::class)
        
        // Mock das extensões Kotlin (auth e firestore)
        mockkStatic("com.google.firebase.auth.AuthKt")
        mockkStatic("com.google.firebase.firestore.FirestoreKt")

        every { Firebase.auth } returns auth
        every { Firebase.firestore } returns db
        every { FirebaseAuth.getInstance() } returns auth
        every { FirebaseFirestore.getInstance() } returns db
        every { db.collection("users") } returns usersCollection
    }

    @After
    fun tearDown() {
        unmockkAll()
    }

    @Test
    fun `login deve retornar erro se e-mail ou senha estiverem vazios`() {
        var successResult = false
        var errorMessage: String? = null

        AuthRepository.login("", "") { success, error ->
            successResult = success
            errorMessage = error
        }

        assertFalse(successResult)
        assertEquals("Preencha login e senha", errorMessage)
    }

    @Test
    fun `login deve retornar sucesso quando Firebase autentica com sucesso`() {
        val email = "teste@teste.com"
        val pass = "123456"
        val task: Task<AuthResult> = mockk(relaxed = true)

        every { auth.signInWithEmailAndPassword(email, pass) } returns task
        every { task.addOnSuccessListener(any()) } returns task
        every { task.addOnFailureListener(any()) } returns task
        
        val successListenerSlot = slot<OnSuccessListener<AuthResult>>()
        every { task.addOnSuccessListener(capture(successListenerSlot)) } answers {
            successListenerSlot.captured.onSuccess(mockk())
            task
        }

        var successResult = false
        AuthRepository.login(email, pass) { success, _ ->
            successResult = success
        }

        assertTrue(successResult)
    }

    @Test
    fun `login deve retornar erro quando Firebase falha na autenticacao`() {
        val email = "erro@teste.com"
        val pass = "errado"
        val task: Task<AuthResult> = mockk(relaxed = true)
        val exception = Exception("Erro de login")

        every { auth.signInWithEmailAndPassword(email, pass) } returns task
        every { task.addOnSuccessListener(any()) } returns task
        every { task.addOnFailureListener(any()) } returns task
        
        val failureListenerSlot = slot<OnFailureListener>()
        every { task.addOnFailureListener(capture(failureListenerSlot)) } answers {
            failureListenerSlot.captured.onFailure(exception)
            task
        }

        var successResult = true
        var errorMessage: String? = null
        AuthRepository.login(email, pass) { success, error ->
            successResult = success
            errorMessage = error
        }

        assertFalse(successResult)
        assertEquals("Login ou senha incorretos", errorMessage)
    }

    @Test
    fun `isLogged deve retornar true se houver usuario logado`() {
        every { auth.currentUser } returns mockk()
        assertTrue(AuthRepository.isLogged())
    }

    @Test
    fun `isLogged deve retornar false se nao houver usuario logado`() {
        every { auth.currentUser } returns null
        assertFalse(AuthRepository.isLogged())
    }

    @Test
    fun `logout deve chamar signOut do FirebaseAuth`() {
        AuthRepository.logout()
        verify { auth.signOut() }
    }
}

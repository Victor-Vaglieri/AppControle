package com.example.controledovitao.data.repository

import com.example.controledovitao.data.model.Payment
import com.google.android.gms.tasks.OnSuccessListener
import com.google.android.gms.tasks.Task
import com.google.firebase.Firebase
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.firestore
import io.mockk.*
import org.junit.After
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class PaymentRepositoryTest {

    private val db: FirebaseFirestore = mockk(relaxed = true)
    private val collection: CollectionReference = mockk(relaxed = true)
    private val document: DocumentReference = mockk(relaxed = true)

    @Before
    fun setup() {
        mockkStatic(Firebase::class)
        mockkStatic(FirebaseFirestore::class)
        mockkStatic("com.google.firebase.firestore.FirestoreKt")
        
        every { Firebase.firestore } returns db
        every { FirebaseFirestore.getInstance() } returns db
        every { db.collection("payment_methods") } returns collection
        every { collection.document(any()) } returns document
    }

    @After
    fun tearDown() {
        unmockkAll()
    }

    @Test
    fun `saveMethod deve retornar true ao salvar com sucesso`() {
        val payment = Payment(name = "Cartão de Crédito")
        val task: Task<DocumentReference> = mockk(relaxed = true)
        
        every { collection.add(payment) } returns task
        every { task.addOnSuccessListener(any()) } returns task
        every { task.addOnFailureListener(any()) } returns task

        val successListenerSlot = slot<OnSuccessListener<DocumentReference>>()
        every { task.addOnSuccessListener(capture(successListenerSlot)) } answers {
            successListenerSlot.captured.onSuccess(mockk())
            task
        }

        var result = false
        PaymentRepository.saveMethod(payment) {
            result = it
        }

        assertTrue(result)
    }

    @Test
    fun `updateMethod deve retornar false se id estiver vazio`() {
        val payment = Payment(id = "", name = "Sem ID")
        var result = true
        PaymentRepository.updateMethod(payment) {
            result = it
        }
        assertFalse(result)
    }

    @Test
    fun `deleteMethod deve chamar delete no documento correto`() {
        val id = "card_123"
        val task: Task<Void> = mockk(relaxed = true)
        every { document.delete() } returns task

        PaymentRepository.deleteMethod(id)
        
        verify { collection.document(id) }
        verify { document.delete() }
    }
}

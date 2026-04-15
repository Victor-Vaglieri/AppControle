package com.example.controledovitao.data.repository

import com.example.controledovitao.data.model.Invest
import com.google.android.gms.tasks.OnFailureListener
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

class InvestmentsRepositoryTest {

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
        every { db.collection("investments") } returns collection
        every { collection.document(any()) } returns document
    }

    @After
    fun tearDown() {
        unmockkAll()
    }

    @Test
    fun `saveInvestment deve retornar true ao salvar com sucesso`() {
        val invest = Invest(id = "1", name = "Teste", value = 100.0)
        val task: Task<DocumentReference> = mockk(relaxed = true)
        
        every { collection.add(invest) } returns task
        every { task.addOnSuccessListener(any()) } returns task
        every { task.addOnFailureListener(any()) } returns task

        val successListenerSlot = slot<OnSuccessListener<DocumentReference>>()
        every { task.addOnSuccessListener(capture(successListenerSlot)) } answers {
            successListenerSlot.captured.onSuccess(mockk())
            task
        }

        var result = false
        InvestmentsRepository.saveInvestment(invest) {
            result = it
        }

        assertTrue(result)
    }

    @Test
    fun `saveInvestment deve retornar false ao falhar`() {
        val invest = Invest(name = "Erro")
        val task: Task<DocumentReference> = mockk(relaxed = true)
        
        every { collection.add(invest) } returns task
        every { task.addOnSuccessListener(any()) } returns task
        every { task.addOnFailureListener(any()) } returns task

        val failureListenerSlot = slot<OnFailureListener>()
        every { task.addOnFailureListener(capture(failureListenerSlot)) } answers {
            failureListenerSlot.captured.onFailure(Exception())
            task
        }

        var result = true
        InvestmentsRepository.saveInvestment(invest) {
            result = it
        }

        assertFalse(result)
    }

    @Test
    fun `updateInvestment deve retornar false se id estiver vazio`() {
        val invest = Invest(id = "")
        var result = true
        InvestmentsRepository.updateInvestment(invest) {
            result = it
        }
        assertFalse(result)
    }

    @Test
    fun `deleteInvestment deve chamar delete no documento correto`() {
        val id = "id_invest"
        val task: Task<Void> = mockk(relaxed = true)
        
        every { document.delete() } returns task
        every { task.addOnSuccessListener(any()) } returns task
        every { task.addOnFailureListener(any()) } returns task

        val successListenerSlot = slot<OnSuccessListener<Void>>()
        every { task.addOnSuccessListener(capture(successListenerSlot)) } answers {
            successListenerSlot.captured.onSuccess(null)
            task
        }

        var result = false
        InvestmentsRepository.deleteInvestment(id) {
            result = it
        }

        verify { collection.document(id) }
        assertTrue(result)
    }
}

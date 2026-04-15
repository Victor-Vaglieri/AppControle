package com.example.controledovitao.data.repository

import com.example.controledovitao.data.model.Options
import com.example.controledovitao.data.model.Payment
import com.google.android.gms.tasks.OnSuccessListener
import com.google.android.gms.tasks.Task
import com.google.firebase.Firebase
import com.google.firebase.firestore.*
import io.mockk.*
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import java.math.BigDecimal

class SpentRepositoryTest {

    private val db: FirebaseFirestore = mockk(relaxed = true)
    private val collection: CollectionReference = mockk(relaxed = true)
    private lateinit var repository: SpentRepository

    @Before
    fun setup() {
        mockkStatic(Firebase::class)
        mockkStatic(FirebaseFirestore::class)

        every { Firebase.firestore } returns db
        every { db.collection("payment_methods") } returns collection
        
        repository = SpentRepository(db)
    }

    @After
    fun tearDown() {
        unmockkAll()
    }

    @Test
    fun `saveExpense deve atualizar saldo e salvar gasto com sucesso`() {
        val methodName = "Cartao Teste"
        val query: Query = mockk(relaxed = true)
        val taskQuery: Task<QuerySnapshot> = mockk(relaxed = true)
        val snapshot: QuerySnapshot = mockk(relaxed = true)
        val doc: QueryDocumentSnapshot = mockk(relaxed = true)
        
        val payment = Payment(
            id = "123",
            name = methodName,
            balance = 1000.0,
            optionType = Options.CREDIT.op,
            usage = 100.0
        )

        every { collection.whereEqualTo("name", methodName) } returns query
        every { query.get() } returns taskQuery
        
        val querySuccessListener = slot<OnSuccessListener<QuerySnapshot>>()
        every { taskQuery.addOnSuccessListener(capture(querySuccessListener)) } returns taskQuery
        
        every { snapshot.isEmpty } returns false
        every { snapshot.documents } returns listOf(doc)
        every { doc.toObject(Payment::class.java) } returns payment
        every { doc.id } returns "doc_id"

        val taskSave: Task<Void> = mockk(relaxed = true)
        every { collection.document("doc_id").set(any()) } returns taskSave
        val saveSuccessListener = slot<OnSuccessListener<Void>>()
        every { taskSave.addOnSuccessListener(capture(saveSuccessListener)) } returns taskSave

        var result = false
        repository.saveExpense("Almoço", methodName, BigDecimal("50.0"), 1, 123456789L) {
            result = it
        }

        querySuccessListener.captured.onSuccess(snapshot)
        saveSuccessListener.captured.onSuccess(null)

        assertTrue(result)
        // No SpentRepository.saveExpense, se for CREDIT, ele NÃO mexe no balance, apenas no usage.
        assertEquals(1000.0, payment.balance, 0.001)
        assertEquals(150.0, payment.usage ?: 0.0, 0.001)
        assertEquals(1, payment.spent.size)
    }
}

package com.example.controledovitao.data.repository

import com.example.controledovitao.data.model.Payment
import com.example.controledovitao.data.model.Spent
import com.example.controledovitao.ui.adapter.ChartData
import com.google.firebase.Firebase
import com.google.firebase.firestore.*
import io.mockk.*
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import java.util.Calendar

class ReportsRepositoryTest {

    private val db: FirebaseFirestore = mockk(relaxed = true)
    private val collection: CollectionReference = mockk(relaxed = true)
    private lateinit var repository: ReportsRepository

    @Before
    fun setup() {
        mockkStatic(Firebase::class)
        mockkStatic(FirebaseFirestore::class)
        
        every { Firebase.firestore } returns db
        every { db.collection("payment_methods") } returns collection
        
        repository = ReportsRepository(db)
    }

    @After
    fun tearDown() {
        unmockkAll()
    }

    @Test
    fun `listenToChartsData deve agrupar gastos por mes corretamente`() {
        val cal = Calendar.getInstance()
    
        cal.set(2024, 0, 15)
        val spentJan = Spent(name = "Aluguel", value = 1000.0, spentDate = cal.timeInMillis)

        cal.set(2024, 1, 10)
        val spentFeb = Spent(name = "Internet", value = 100.0, spentDate = cal.timeInMillis)

        val payment = Payment(spent = mutableListOf(spentJan, spentFeb))
        val snapshot: QuerySnapshot = mockk(relaxed = true)
        val doc: QueryDocumentSnapshot = mockk(relaxed = true)

        val listenerSlot = slot<EventListener<QuerySnapshot>>()
        every { collection.addSnapshotListener(capture(listenerSlot)) } returns mockk()

        every { snapshot.documents } returns listOf(doc)
        every { doc.toObject(Payment::class.java) } returns payment

        var chartDataResult = listOf<ChartData>()
        repository.listenToChartsData {
            chartDataResult = it
        }

        listenerSlot.captured.onEvent(snapshot, null)

        assertEquals(2, chartDataResult.size)
        
        val months = chartDataResult.map { it.month.lowercase() }
        assertTrue(months.contains("janeiro"))
        assertTrue(months.contains("fevereiro"))
    }
}

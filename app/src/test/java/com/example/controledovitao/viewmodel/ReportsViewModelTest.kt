package com.example.controledovitao.viewmodel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.example.controledovitao.data.repository.ReportsRepository
import com.example.controledovitao.ui.adapter.ChartData
import io.mockk.*
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class ReportsViewModelTest {

    @get:Rule
    val rule = InstantTaskExecutorRule()

    private val repository = mockk<ReportsRepository>(relaxed = true)
    private lateinit var viewModel: ReportsViewModel

    @Before
    fun setup() {
        every { repository.listenToChartsData(any()) } returns Unit
        every { repository.listenToExportData(any()) } returns Unit
        viewModel = ReportsViewModel(repository)
    }

    @After
    fun tearDown() {
        unmockkAll()
    }

    @Test
    fun `startListening deve atualizar graficos quando o repositorio retornar dados`() {
        val chartData = listOf(ChartData("Jan", listOf(100f)))
        val callback = slot<(List<ChartData>) -> Unit>()
        every { repository.listenToChartsData(capture(callback)) } answers {
            callback.captured(chartData)
        }

        viewModel = ReportsViewModel(repository)

        assertEquals(1, viewModel.charts.value?.size)
        assertEquals("Jan", viewModel.charts.value?.get(0)?.month)
    }

    @Test
    fun `changeLimit deve respeitar o intervalo de 0 a 100`() {
        viewModel.changeLimit(50) // 80 + 50 = 130 -> 100
        assertEquals(100, viewModel.limitAlert.value)

        viewModel.changeLimit(-150)
        assertEquals(0, viewModel.limitAlert.value)
    }
}

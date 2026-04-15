package com.example.controledovitao.viewmodel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.example.controledovitao.data.model.Invest
import com.example.controledovitao.data.repository.InvestmentsRepository
import io.mockk.*
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import java.math.BigDecimal

class InvestmentsViewModelTest {

    @get:Rule
    val rule = InstantTaskExecutorRule()

    private lateinit var viewModel: InvestmentsViewModel

    @Before
    fun setup() {
        mockkObject(InvestmentsRepository)
        every { InvestmentsRepository.listenToInvestments(any()) } returns Unit
        viewModel = InvestmentsViewModel()
    }

    @After
    fun tearDown() {
        unmockkAll()
    }

    @Test
    fun `calculateTotals deve somar valores investidos e estimados corretamente`() {
        val investments = listOf(
            Invest(id = "1", name = "BTC", value = 1000.0, estimate = 1500.0),
            Invest(id = "2", name = "CDB", value = 500.0, estimate = 550.0)
        )
        val callback = slot<(List<Invest>) -> Unit>()
        every { InvestmentsRepository.listenToInvestments(capture(callback)) } answers {
            callback.captured(investments)
        }
        viewModel = InvestmentsViewModel() // Re-init to trigger captured callback
        assertEquals(BigDecimal.valueOf(1500.0), viewModel.totalInvested.value)
        assertEquals(BigDecimal.valueOf(2050.0), viewModel.totalEstimated.value)
        assertEquals(BigDecimal.valueOf(550.0), viewModel.totalProfit.value)
    }
}

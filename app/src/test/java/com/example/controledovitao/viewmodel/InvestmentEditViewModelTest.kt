package com.example.controledovitao.viewmodel

import android.app.Application
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.example.controledovitao.data.model.SimulationOption
import com.example.controledovitao.data.model.SimulationType
import com.example.controledovitao.data.repository.InvestmentsRepository
import com.example.controledovitao.data.repository.SimulationRepository
import io.mockk.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import java.math.BigDecimal

@OptIn(ExperimentalCoroutinesApi::class)
class InvestmentEditViewModelTest {

    @get:Rule
    val rule = InstantTaskExecutorRule()

    private val testDispatcher = StandardTestDispatcher()
    private val application = mockk<Application>(relaxed = true)
    private lateinit var viewModel: InvestmentEditViewModel

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        mockkObject(InvestmentsRepository)
        mockkConstructor(SimulationRepository::class)
        coEvery { anyConstructed<SimulationRepository>().getOptions(any()) } returns emptyList()
        viewModel = InvestmentEditViewModel(application)
    }

    @After
    fun tearDown() {
        unmockkAll()
        Dispatchers.resetMain()
    }

    @Test
    fun `initData deve parsear o periodo P1Y2M corretamente`() {
        viewModel.initData("1", "Invest", 1000.0, "P1Y2M")
        
        assertEquals(1, viewModel.inputYears.value)
        assertEquals(2, viewModel.inputMonths.value)
        assertEquals(BigDecimal.valueOf(1000.0), viewModel.inputValue.value)
    }

    @Test
    fun `calculate deve atualizar valores quando taxa for encontrada`() {
        val options = listOf(SimulationOption("1", "CDB", SimulationType.BANCO.name, 0.12))
        coEvery { anyConstructed<SimulationRepository>().getOptions(SimulationType.BANCO) } returns options
        coEvery { anyConstructed<SimulationRepository>().getOptions(SimulationType.CRIPTO) } returns emptyList()
        viewModel.initData("1", "CDB", 1000.0, "P1Y0M")
        testDispatcher.scheduler.advanceUntilIdle()
        
        // 1000 * (1 + 0.12) = 1120
        assertEquals(1120.0, viewModel.resultTotal.value?.toDouble() ?: 0.0, 0.1)
        assertEquals(120.0, viewModel.resultYield.value?.toDouble() ?: 0.0, 0.1)
    }
}

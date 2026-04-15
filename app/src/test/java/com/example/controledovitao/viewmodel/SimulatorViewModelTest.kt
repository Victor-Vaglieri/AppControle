package com.example.controledovitao.viewmodel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.example.controledovitao.data.model.SimulationOption
import com.example.controledovitao.data.model.SimulationType
import com.example.controledovitao.data.repository.SimulationRepository
import io.mockk.coEvery
import io.mockk.mockkConstructor
import io.mockk.unmockkAll
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
import android.app.Application
import io.mockk.mockk

@OptIn(ExperimentalCoroutinesApi::class)
class SimulatorViewModelTest {

    @get:Rule
    val rule = InstantTaskExecutorRule()

    private val testDispatcher = StandardTestDispatcher()
    private lateinit var viewModel: SimulatorViewModel
    private val application = mockk<Application>(relaxed = true)

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        mockkConstructor(SimulationRepository::class)
        
        val mockOptions = listOf(
            SimulationOption("1", "CDB 100%", SimulationType.BANCO.name, 0.10)
        )
        coEvery { anyConstructed<SimulationRepository>().getOptions(any()) } returns mockOptions

        viewModel = SimulatorViewModel(application)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
        unmockkAll()
    }

    @Test
    fun `loadOptions deve carregar opcoes e selecionar a primeira`() {
        testDispatcher.scheduler.advanceUntilIdle()

        assertEquals(1, viewModel.availableOptions.value?.size)
        assertEquals("CDB 100%", viewModel.selectedOption.value?.name)
        assertEquals("10,00% a.a.", viewModel.infoRate.value)
    }

    @Test
    fun `calculate deve calcular rendimento corretamente para 1 ano`() {
        testDispatcher.scheduler.advanceUntilIdle()
        viewModel.setExactValue(BigDecimal("1000.00"))
        viewModel.inputYears.value = 1
        viewModel.inputMonths.value = 0

        // 1000 * (1 + 0.10) = 1100
        val total = viewModel.resultTotal.value?.toDouble() ?: 0.0
        assertEquals(1100.0, total, 0.1)
        assertEquals(100.0, viewModel.resultYield.value?.toDouble() ?: 0.0, 0.1)
    }
}

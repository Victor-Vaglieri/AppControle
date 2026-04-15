package com.example.controledovitao.viewmodel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.example.controledovitao.data.repository.SpentRepository
import io.mockk.*
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import java.math.BigDecimal

class SpentViewModelTest {

    @get:Rule
    val rule = InstantTaskExecutorRule()

    private val repository = mockk<SpentRepository>(relaxed = true)
    private lateinit var viewModel: SpentViewModel

    @Before
    fun setup() {
        viewModel = SpentViewModel(repository)
    }

    @After
    fun tearDown() {
        unmockkAll()
    }

    @Test
    fun `saveExpense deve retornar erro se titulo for vazio`() {
        viewModel.saveExpense("", "Cartao", BigDecimal("10.0"), 1, 0L)
        assertEquals("O título não pode estar vazio", viewModel.errorMessage.value)
    }

    @Test
    fun `saveExpense deve chamar o repositorio se dados forem validos`() {
        val callback = slot<(Boolean) -> Unit>()
        every { 
            repository.saveExpense(any(), any(), any(), any(), any(), capture(callback)) 
        } answers {
            callback.captured(true)
        }

        viewModel.saveExpense("Lanche", "Nubank", BigDecimal("25.0"), 1, 123456789L)

        verify { repository.saveExpense("Lanche", "Nubank", any(), 1, 123456789L, any()) }
        assertTrue(viewModel.saveSuccess.value == true)
    }
}

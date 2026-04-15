package com.example.controledovitao.viewmodel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.example.controledovitao.data.model.Options
import com.example.controledovitao.data.model.Payment
import com.example.controledovitao.data.repository.InvestmentsRepository
import com.example.controledovitao.data.repository.PaymentRepository
import io.mockk.every
import io.mockk.mockkObject
import io.mockk.slot
import io.mockk.unmockkAll
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import java.math.BigDecimal

class HomeViewModelTest {

    @get:Rule
    val rule = InstantTaskExecutorRule()

    private lateinit var viewModel: HomeViewModel

    @Before
    fun setup() {
        mockkObject(PaymentRepository)
        mockkObject(InvestmentsRepository)
        every { PaymentRepository.listenToMethods(any()) } returns Unit
        every { InvestmentsRepository.listenToInvestments(any()) } returns Unit

        viewModel = HomeViewModel()
    }

    @After
    fun tearDown() {
        unmockkAll()
    }

    @Test
    fun `calculatePaymentTotals deve somar saldos e limites corretamente`() {
        val payments = listOf(
            Payment(id = "1", name = "Cartão A", optionType = Options.CREDIT.op, balance = 100.0, limit = 1000.0, usage = 200.0),
            Payment(id = "2", name = "Dinheiro", optionType = Options.MONEY.op, balance = 50.0)
        )
        
        val callback = slot<(List<Payment>) -> Unit>()
        every { PaymentRepository.listenToMethods(capture(callback)) } answers {
            callback.captured(payments)
        }

        // Re-inicializa para disparar o listener mockado
        viewModel = HomeViewModel()
        assertEquals(BigDecimal.valueOf(150.0), viewModel.totalBalance.value)
        assertEquals(BigDecimal.valueOf(1000.0), viewModel.totalLimit.value)
        assertEquals(BigDecimal.valueOf(200.0), viewModel.totalUsage.value)
    }

    @Test
    fun `findBestCard deve retornar o cartao com maior limite disponivel se estiver no periodo de fechamento`() {
        val payments = listOf(
            Payment(id = "1", name = "Cartão Ruim", optionType = Options.CREDIT.op, limit = 1000.0, usage = 900.0, bestDate = 10, shutdown = 20),
            Payment(id = "2", name = "Cartão Bom", optionType = Options.CREDIT.op, limit = 5000.0, usage = 100.0, bestDate = 10, shutdown = 20)
        )
        
        val callback = slot<(List<Payment>) -> Unit>()
        every { PaymentRepository.listenToMethods(capture(callback)) } answers {
            callback.captured(payments)
        }

        viewModel = HomeViewModel()

        assertEquals("Cartão Bom", viewModel.bestCardName.value)
    }
}

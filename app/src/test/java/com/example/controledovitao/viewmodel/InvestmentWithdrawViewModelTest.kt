package com.example.controledovitao.viewmodel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.example.controledovitao.data.model.Invest
import com.example.controledovitao.data.model.Payment
import com.example.controledovitao.data.repository.InvestmentsRepository
import com.example.controledovitao.data.repository.PaymentRepository
import io.mockk.*
import org.junit.After
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import java.math.BigDecimal

class InvestmentWithdrawViewModelTest {

    @get:Rule
    val rule = InstantTaskExecutorRule()

    private lateinit var viewModel: InvestmentWithdrawViewModel

    @Before
    fun setup() {
        mockkObject(InvestmentsRepository)
        mockkObject(PaymentRepository)
        every { PaymentRepository.listenToMethods(any()) } returns Unit
        viewModel = InvestmentWithdrawViewModel()
    }

    @After
    fun tearDown() {
        unmockkAll()
    }

    @Test
    fun `confirmWithdrawal deve atualizar saldo do pagamento e deletar investimento se resgate for total`() {
        val payment = Payment(id = "p1", name = "Conta", balance = 100.0)
        val investId = "i1"
        viewModel.initData(investId, "Invest", 1000.0, 1100.0)
        viewModel.selectPayment(payment)
        viewModel.setExactWithdrawValue(BigDecimal("1100.0"))

        val payCallback = slot<(Boolean) -> Unit>()
        every { PaymentRepository.updateMethod(any(), capture(payCallback)) } answers {
            payCallback.captured(true)
        }
        every { InvestmentsRepository.deleteInvestment(investId, any()) } returns Unit
        viewModel.confirmWithdrawal()
        verify { PaymentRepository.updateMethod(match { it.balance == 1200.0 }, any()) }
        verify { InvestmentsRepository.deleteInvestment(investId) }
        assertTrue(viewModel.withdrawStatus.value == true)
    }
}

package com.example.controledovitao.viewmodel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.example.controledovitao.data.model.Options
import com.example.controledovitao.data.model.Payment
import com.example.controledovitao.data.repository.PaymentRepository
import io.mockk.*
import org.junit.After
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class PaymentViewModelTest {

    @get:Rule
    val rule = InstantTaskExecutorRule()

    private lateinit var viewModel: PaymentViewModel

    @Before
    fun setup() {
        mockkObject(PaymentRepository)
        every { PaymentRepository.listenToMethods(any()) } returns Unit
        viewModel = PaymentViewModel()
    }

    @After
    fun tearDown() {
        unmockkAll()
    }

    @Test
    fun `createPayment deve chamar saveMethod no repositorio`() {
        val callback = slot<(Boolean) -> Unit>()
        every { PaymentRepository.saveMethod(any(), capture(callback)) } answers {
            callback.captured(true)
        }
        viewModel.createPayment("Novo Cartao", Options.CREDIT.op, 5000.0, 0.0, 10, 20)
        verify { PaymentRepository.saveMethod(match { it.name == "Novo Cartao" }, any()) }
        assertTrue(viewModel.operationStatus.value == true)
    }
}

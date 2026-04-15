package com.example.controledovitao.viewmodel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.example.controledovitao.data.repository.AuthRepository
import io.mockk.every
import io.mockk.mockk
import io.mockk.slot
import io.mockk.unmockkAll
import io.mockk.verify
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class LoginViewModelTest {

    @get:Rule
    val rule = InstantTaskExecutorRule()

    private val repository = mockk<AuthRepository>(relaxed = true)
    private lateinit var viewModel: LoginViewModel

    @Before
    fun setup() {
        viewModel = LoginViewModel(repository)
    }

    @After
    fun tearDown() {
        unmockkAll()
    }

    @Test
    fun `doLogin deve exibir erro se campos estiverem vazios`() {
        viewModel.doLogin("", "")

        assertEquals("Preencha todos os campos", viewModel.errorMessage.value)
        assertFalse(viewModel.isLoading.value ?: true)
    }

    @Test
    fun `doLogin deve disparar sucesso quando o repositorio retorna true`() {
        val email = "teste@teste.com"
        val pass = "123456"
        val callback = slot<(Boolean, String?) -> Unit>()
        every { 
            repository.login(email, pass, capture(callback)) 
        } answers {
            callback.captured(true, null)
        }
        viewModel.doLogin(email, pass)
        assertTrue(viewModel.loginSuccess.value ?: false)
        assertFalse(viewModel.isLoading.value ?: true)
        verify { repository.login(email, pass, any()) }
    }

    @Test
    fun `doLogin deve disparar erro quando o repositorio retorna falha`() {
        val email = "erro@teste.com"
        val pass = "errado"
        val callback = slot<(Boolean, String?) -> Unit>()

        every { 
            repository.login(email, pass, capture(callback)) 
        } answers {
            callback.captured(false, "Senha incorreta")
        }
        viewModel.doLogin(email, pass)

        assertEquals("Senha incorreta", viewModel.errorMessage.value)
        assertFalse(viewModel.loginSuccess.value ?: true)
    }
}

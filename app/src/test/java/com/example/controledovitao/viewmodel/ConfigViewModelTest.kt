package com.example.controledovitao.viewmodel

import android.app.Application
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.example.controledovitao.data.repository.ConfigRepository
import io.mockk.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class ConfigViewModelTest {

    @get:Rule
    val rule = InstantTaskExecutorRule()

    private val testDispatcher = StandardTestDispatcher()
    private val application = mockk<Application>(relaxed = true)
    private val repository = mockk<ConfigRepository>(relaxed = true)
    private lateinit var viewModel: ConfigViewModel

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        
        // Mock dos Flows para não quebrar o asLiveData()
        every { repository.isThemeDark } returns flowOf(false)
        every { repository.isBackupEnabled } returns flowOf(true)
        every { repository.isBiometricEnabled } returns flowOf(false)
        every { repository.isDataCollectionEnabled } returns flowOf(true)
        every { repository.getProfileImage() } returns null
        coEvery { repository.getUserName() } returns "Vitao"

        viewModel = ConfigViewModel(application, repository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
        unmockkAll()
    }

    @Test
    fun `loadUserData deve carregar nome do usuario corretamente`() {
        testDispatcher.scheduler.advanceUntilIdle()
        assertEquals("Vitao", viewModel.userName.value)
    }

    @Test
    fun `toggleTheme deve chamar o repositorio`() {
        viewModel.toggleTheme(true)
        testDispatcher.scheduler.advanceUntilIdle()
        coVerify { repository.saveThemePreference(true) }
    }
}

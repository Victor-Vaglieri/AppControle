package com.example.controledovitao.viewmodel

import android.app.Application
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.example.controledovitao.data.model.Notification
import com.example.controledovitao.data.repository.NotificationsRepository
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
class NotificationsViewModelTest {

    @get:Rule
    val rule = InstantTaskExecutorRule()

    private val testDispatcher = StandardTestDispatcher()
    private val application = mockk<Application>(relaxed = true)
    private val repository = mockk<NotificationsRepository>(relaxed = true)
    private lateinit var viewModel: NotificationsViewModel

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        
        every { repository.isPushEnabled } returns flowOf(true)
        every { repository.isEmailEnabled } returns flowOf(false)
        every { repository.listenToNotifications(any()) } returns Unit

        viewModel = NotificationsViewModel(application, repository)
    }

    @After
    fun tearDown() {
        unmockkAll()
        Dispatchers.resetMain()
    }

    @Test
    fun `loadNotifications deve atualizar lista quando o repositorio disparar`() {
        val notifications = listOf(Notification(title = "Alerta", description = "Teste"))
        val callback = slot<(List<Notification>) -> Unit>()
        every { repository.listenToNotifications(capture(callback)) } answers {
            callback.captured(notifications)
        }

        viewModel = NotificationsViewModel(application, repository)
        assertEquals(1, viewModel.notifications.value?.size)
    }
}

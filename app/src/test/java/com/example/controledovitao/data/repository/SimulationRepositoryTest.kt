package com.example.controledovitao.data.repository

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import com.example.controledovitao.data.api.FinancialApi
import com.example.controledovitao.data.api.RetrofitClient
import com.example.controledovitao.data.api.TaxaBrasilApi
import com.example.controledovitao.data.model.SimulationType
import io.mockk.*
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class SimulationRepositoryTest {

    private val context: Context = mockk(relaxed = true)
    private val dataStore: DataStore<Preferences> = mockk(relaxed = true)
    private val brasilApi: FinancialApi = mockk()
    private val cryptoApi: FinancialApi = mockk()
    private lateinit var repository: SimulationRepository

    @Before
    fun setup() {
        mockkObject(RetrofitClient)
        mockkStatic("androidx.datastore.preferences.core.PreferencesKt")
        
        every { RetrofitClient.brasilApi } returns brasilApi
        every { RetrofitClient.cryptoApi } returns cryptoApi
        
        // Mock do DataStore
        every { dataStore.data } returns flowOf(mockk(relaxed = true))
        coEvery { dataStore.edit(any()) } returns mockk()

        repository = SimulationRepository(context, dataStore)
    }

    @After
    fun tearDown() {
        unmockkAll()
    }

    @Test
    fun `getOptions BANCO deve retornar dados da API com sucesso`() = runTest {
        val mockTaxas = listOf(
            TaxaBrasilApi("Selic", 11.75),
            TaxaBrasilApi("CDI", 11.65)
        )
        coEvery { brasilApi.getTaxasBr() } returns mockTaxas

        val result = repository.getOptions(SimulationType.BANCO)

        assertNotNull(result)
        assertEquals(3, result.size)
        assertEquals("Tesouro Selic", result[2].name)
        assertEquals(0.1175, result[2].annualRate, 0.0001)
    }

    @Test
    fun `getOptions CRIPTO deve retornar dados da API com sucesso`() = runTest {
        coEvery { cryptoApi.getCryptos() } returns listOf(
            mockk(relaxed = true) {
                every { id } returns "bitcoin"
                every { name } returns "Bitcoin"
                every { symbol } returns "btc"
                every { price_change_percentage_1y_in_currency } returns 50.0
            }
        )

        val result = repository.getOptions(SimulationType.CRIPTO)

        assertNotNull(result)
        assertEquals(1, result.size)
        assertEquals("Bitcoin (BTC)", result[0].name)
        assertEquals(0.5, result[0].annualRate, 0.0001)
    }

    @Test
    fun `getOptions deve usar fallback quando API falha e nao ha cache`() = runTest {
        coEvery { brasilApi.getTaxasBr() } throws Exception("Erro de rede")
        every { dataStore.data } returns flowOf(mockk {
            every { get(any<Preferences.Key<String>>()) } returns null
        })

        val result = repository.getOptions(SimulationType.BANCO)

        assertNotNull(result)
        assertEquals(3, result.size)
        assertTrue(result[0].name.contains("Padrão"))
    }
}

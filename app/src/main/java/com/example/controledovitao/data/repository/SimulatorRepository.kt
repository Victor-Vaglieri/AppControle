package com.example.controledovitao.data.repository

import android.content.Context
import android.util.Log
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.example.controledovitao.data.api.RetrofitClient
import com.example.controledovitao.data.model.SimulationOption
import com.example.controledovitao.data.model.SimulationType
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
private val Context.dataStoreCache by preferencesDataStore(name = "simulation_cache")

class SimulationRepository(private val context: Context) {

    private val gson = Gson()

    companion object {
        val KEY_CACHE_BANCO = stringPreferencesKey("cache_banco_json")
        val KEY_CACHE_CRIPTO = stringPreferencesKey("cache_cripto_json")
    }

    suspend fun getOptions(type: SimulationType): List<SimulationOption> {
        return withContext(Dispatchers.IO) {
            try {
                val freshData = if (type == SimulationType.BANCO) {
                    fetchBankDataFromApi()
                } else {
                    fetchCryptoDataFromApi()
                }

                saveToCache(type, freshData)
                Log.d("SimRepo", "Dados atualizados via API e salvos no cache.")

                freshData

            } catch (e: Exception) {
                Log.e("SimRepo", "Sem internet. Tentando cache local...", e)
                val cachedData = loadFromCache(type)

                if (cachedData.isNotEmpty()) {
                    Log.d("SimRepo", "Usando dados do Cache (última atualização).")
                    cachedData
                } else {
                    Log.d("SimRepo", "Sem cache. Usando dados Hardcoded.")
                    getHardcodedFallback(type)
                }
            }
        }
    }

    private suspend fun saveToCache(type: SimulationType, list: List<SimulationOption>) {
        val key = if (type == SimulationType.BANCO) KEY_CACHE_BANCO else KEY_CACHE_CRIPTO
        val jsonString = gson.toJson(list)

        context.dataStoreCache.edit { prefs ->
            prefs[key] = jsonString
        }
    }
    private suspend fun loadFromCache(type: SimulationType): List<SimulationOption> {
        val key = if (type == SimulationType.BANCO) KEY_CACHE_BANCO else KEY_CACHE_CRIPTO

        val jsonString = context.dataStoreCache.data.map { prefs ->
            prefs[key] ?: ""
        }.first()

        return if (jsonString.isNotEmpty()) {
            val listType = object : TypeToken<List<SimulationOption>>() {}.type
            gson.fromJson(jsonString, listType)
        } else {
            emptyList()
        }
    }

    private suspend fun fetchBankDataFromApi(): List<SimulationOption> {
        val taxas = RetrofitClient.brasilApi.getTaxasBr()

        val selic = taxas.find { it.nome == "Selic" }?.valor ?: 10.0
        val cdi = taxas.find { it.nome == "CDI" }?.valor ?: (selic - 0.10)

        val taxaPoupanca = if (selic > 8.5) 0.0617 else (selic * 0.70) / 100.0
        val taxaCdiDecimal = cdi / 100.0
        val taxaSelicDecimal = selic / 100.0

        return listOf(
            SimulationOption("1", "Poupança (Atual)", SimulationType.BANCO.name, taxaPoupanca),
            SimulationOption("2", "CDB (100% CDI)", SimulationType.BANCO.name, taxaCdiDecimal),
            SimulationOption("3", "Tesouro Selic", SimulationType.BANCO.name, taxaSelicDecimal)
        )
    }

    private suspend fun fetchCryptoDataFromApi(): List<SimulationOption> {
        val cryptos = RetrofitClient.cryptoApi.getCryptos()
        return cryptos.map { coin ->
            val annualRate = (coin.price_change_percentage_1y_in_currency ?: 0.0) / 100.0
            SimulationOption(
                id = coin.id,
                name = "${coin.name} (${coin.symbol.uppercase()})",
                typeString = SimulationType.CRIPTO.name,
                annualRate = annualRate
            )
        }
    }

    private fun getHardcodedFallback(type: SimulationType): List<SimulationOption> {
        return if (type == SimulationType.BANCO) {
            listOf(
                SimulationOption("1", "Poupança (Padrão)", SimulationType.BANCO.name, 0.0617),
                SimulationOption("2", "CDB (Padrão)", SimulationType.BANCO.name, 0.105),
                SimulationOption("3", "Tesouro (Padrão)", SimulationType.BANCO.name, 0.11)
            )
        } else {
            listOf(
                SimulationOption("4", "Bitcoin (Padrão)", SimulationType.CRIPTO.name, 0.60),
                SimulationOption("5", "Ethereum (Padrão)", SimulationType.CRIPTO.name, 0.45),
                SimulationOption("6", "Solana (Padrão)", SimulationType.CRIPTO.name, 0.80)
            )
        }
    }
}
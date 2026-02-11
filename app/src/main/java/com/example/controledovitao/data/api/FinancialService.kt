package com.example.controledovitao.data.api

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

data class TaxaBrasilApi(
    val nome: String,
    val valor: Double
)

data class CryptoCoinGecko(
    val id: String,
    val symbol: String,
    val name: String,
    val price_change_percentage_1y_in_currency: Double?
)


interface FinancialApi {

    @GET("api/taxas/v1")
    suspend fun getTaxasBr(): List<TaxaBrasilApi>
    @GET("api/v3/coins/markets")
    suspend fun getCryptos(
        @Query("vs_currency") currency: String = "brl",
        @Query("ids") ids: String = "bitcoin,ethereum,solana",
        @Query("price_change_percentage") range: String = "1y"
    ): List<CryptoCoinGecko>
}

object RetrofitClient {

    private val retrofitBrasil = Retrofit.Builder()
        .baseUrl("https://brasilapi.com.br/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    val brasilApi: FinancialApi = retrofitBrasil.create(FinancialApi::class.java)


    private val retrofitCrypto = Retrofit.Builder()
        .baseUrl("https://api.coingecko.com/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    val cryptoApi: FinancialApi = retrofitCrypto.create(FinancialApi::class.java)
}
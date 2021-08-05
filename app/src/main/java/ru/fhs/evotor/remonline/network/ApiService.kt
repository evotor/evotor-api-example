package ru.fhs.evotor.remonline.network

import retrofit2.http.GET
import ru.evotor.framework.receipt.Receipt

interface ApiService {
    @GET("receipts")
    suspend fun getReceipts(): List<Receipt>
}
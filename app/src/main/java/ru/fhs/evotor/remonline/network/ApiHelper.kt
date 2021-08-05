package ru.fhs.evotor.remonline.network

class ApiHelper(private val apiService: ApiService) {

    suspend fun getReceipts() = apiService.getReceipts()
}
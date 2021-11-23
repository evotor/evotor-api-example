package ru.fhs.evotor.remonline.network

import android.content.Context
import okhttp3.Response
import ru.fhs.evotor.remonline.model.ReceiptModel
import ru.fhs.evotor.remonline.network.data.ResponseResult
import ru.fhs.evotor.remonline.network.data.request.InitDeviceRequest
import ru.fhs.evotor.remonline.network.data.response.BaseResponse
import ru.fhs.evotor.remonline.network.data.response.InitDeviceResponse
import ru.fhs.evotor.remonline.prefs.AppSettings

class ApiHelper(context: Context, private val apiService: ApiService) {
    private val appSettings = AppSettings(context)

    val hasApiCode: Boolean
        get() = appSettings.apiKey.isNotBlank()

    fun saveApiKey(apiKey: String) {
        appSettings.apiKey = apiKey
    }

    suspend fun getReceipts(): ResponseResult<List<ReceiptModel>> {
        return processResponse { apiService.getReceipts() }
    }

    suspend fun initDevice(request: InitDeviceRequest): ResponseResult<InitDeviceResponse> {
        return processResponse { apiService.initDevice(request) }
    }

    private suspend fun <T> processResponse(responseFun: suspend () -> BaseResponse<T>): ResponseResult<T> {
        return try {
            val result = responseFun()
            if (result.isSuccess && result.data != null) {
                ResponseResult(result.data)
            } else {
                ResponseResult(null, result.errorMessage)
            }
        } catch (exc: Exception) {
            ResponseResult(null, exc.message)
        }
    }
}
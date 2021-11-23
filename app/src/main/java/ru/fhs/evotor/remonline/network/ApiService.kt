package ru.fhs.evotor.remonline.network

import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import ru.evotor.framework.receipt.Receipt
import ru.fhs.evotor.remonline.model.ReceiptModel
import ru.fhs.evotor.remonline.network.data.request.InitDeviceRequest
import ru.fhs.evotor.remonline.network.data.response.BaseResponse
import ru.fhs.evotor.remonline.network.data.response.InitDeviceResponse

interface ApiService {
    @GET("receipt/new")
    suspend fun getReceipts(): BaseResponse<List<ReceiptModel>>

    @POST("device/init")
    suspend fun initDevice(@Body request: InitDeviceRequest): BaseResponse<InitDeviceResponse>
}
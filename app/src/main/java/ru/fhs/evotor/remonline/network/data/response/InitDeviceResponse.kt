package ru.fhs.evotor.remonline.network.data.response


import com.google.gson.annotations.SerializedName
import com.google.gson.annotations.Expose

data class InitDeviceResponse(
    @SerializedName("api_key")
    @Expose
    val code: String
)
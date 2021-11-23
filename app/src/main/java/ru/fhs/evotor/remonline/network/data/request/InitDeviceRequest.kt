package ru.fhs.evotor.remonline.network.data.request


import com.google.gson.annotations.SerializedName
import com.google.gson.annotations.Expose

data class InitDeviceRequest(
    @SerializedName("code")
    @Expose
    val code: Int
)
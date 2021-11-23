package ru.fhs.evotor.remonline.network.data.response

import com.google.gson.annotations.SerializedName

class BaseResponse<T>(
        @SerializedName("data") private val _data: T?,
        @SerializedName("errorMessage") private val _errorMessage: String?,
        @SerializedName("status") val isSuccess: Boolean) {

    val data: T
        get() {
            return _data ?: throw NullPointerException()
        }

    val errorMessage: String
        get() = _errorMessage.orEmpty()
}


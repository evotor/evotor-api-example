package ru.fhs.evotor.remonline.network.data

data class ResponseResult<T>(
    val data: T? = null,
    val error: String? = null
) {
    val unwrappedData: T
        get() = data!!

    val isSuccess: Boolean
        get() = data != null
}
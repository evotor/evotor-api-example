package ru.fhs.evotor.remonline.model

import com.google.gson.annotations.SerializedName

data class ReceiptModel(
    val title: String,
    val total: Int,
    val date: Long,
    @SerializedName("is_product")
    val isProduct: Boolean,
    val items: List<ReceiptElementModel>
)
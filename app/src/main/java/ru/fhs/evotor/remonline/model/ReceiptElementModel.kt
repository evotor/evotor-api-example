package ru.fhs.evotor.remonline.model


import com.google.gson.annotations.SerializedName
import com.google.gson.annotations.Expose

data class ReceiptElementModel(
    @SerializedName("cost")
    val cost: Int?,
    @SerializedName("is_product")
    val isProduct: Boolean?,
    @SerializedName("name")
    val name: String?,
    @SerializedName("price")
    val price: Int?,
    @SerializedName("qty")
    val qty: Int?
)
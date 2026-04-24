package com.smartkup.smartkup.model

import com.google.gson.annotations.SerializedName

data class PantryItem(
    @SerializedName("pantry_item_id") val pantryItemId: Long,
    val product: Product?,
    val quantity: Double,
    val unit: String?,
    @SerializedName("expiration_date") val expirationDate: String?
)
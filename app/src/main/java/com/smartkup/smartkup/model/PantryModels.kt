package com.smartkup.smartkup.model

import com.google.gson.annotations.SerializedName

// Matches your `PantryItems` table, but expects the backend
// to send the full nested Product object, not just the ID!
data class PantryItem(
    @SerializedName("pantry_item_id") val pantryItemId: Long,
    val product: Product?, // Uses the Product from SharedModels.kt
    val quantity: Double,
    val unit: String?,
    @SerializedName("expiration_date") val expirationDate: String?
)
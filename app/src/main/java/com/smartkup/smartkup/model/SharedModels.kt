package com.smartkup.smartkup.model

import com.google.gson.annotations.SerializedName

// Matches your `Categories` table
data class Category(
    @SerializedName("category_id", alternate = ["categoryId"]) val categoryId: Long,
    val name: String
)

// Matches your `Products` table
data class Product(
    @SerializedName("product_id") val productId: Long,
    val name: String,
    @SerializedName("category_id") val categoryId: Long?,
    @SerializedName("default_unit") val defaultUnit: String?
)

// Matches your `Shops` table
data class Shop(
    // This allows Android to read BOTH "shop_id" (snake) and "shopId" (camel) from the JSON
    @SerializedName("shop_id", alternate = ["shopId"]) val shopId: Long,
    @SerializedName("user_id", alternate = ["userId"]) val userId: Long?,
    val name: String
)
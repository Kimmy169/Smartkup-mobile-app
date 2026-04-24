package com.smartkup.smartkup.model

import com.google.gson.annotations.SerializedName

data class Category(
    @SerializedName("category_id", alternate = ["categoryId"]) val categoryId: Long,
    val name: String
)

data class Product(
    @SerializedName("product_id") val productId: Long,
    val name: String,
    @SerializedName("category_id") val categoryId: Long?,
    @SerializedName("default_unit") val defaultUnit: String?,
    val price: Double?
)

data class Shop(
    @SerializedName("shop_id", alternate = ["shopId"]) val shopId: Long,
    @SerializedName("user_id", alternate = ["userId"]) val userId: Long?,
    val name: String
)
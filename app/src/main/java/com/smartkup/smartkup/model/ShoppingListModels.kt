package com.smartkup.smartkup.model

import com.google.gson.annotations.SerializedName

// Matches your `ShoppingLists` table
data class ShoppingListDetails(
    @SerializedName("list_id") val listId: Long,
    @SerializedName("user_id") val userId: Long?,
    val name: String,
    val status: String?
)

// Matches your `ShoppingListItems` table + the productName from Spring Boot
data class ShoppingListItem(
    @SerializedName("list_item_id") val itemId: Long,
    @SerializedName("list_id") val listId: Long,
    @SerializedName("product_id") val productId: Long,
    val productName: String?,
    @SerializedName("category_id", alternate = ["categoryId"]) val categoryId: Long?,
    @SerializedName("shop_id") val shopId: Long?,
    val quantity: Double,
    val unit: String?,
    @SerializedName("estimated_price") val estimatedPrice: Double?,
    @SerializedName("actual_price") val actualPrice: Double?,
    @SerializedName("is_purchased") val purchased: Boolean,
    @SerializedName("added_from_pantry") val addedFromPantry: Boolean?
)

// Matches the custom DTO your Spring Boot controller returns
data class ShoppingListResponseDTO(
    val listDetails: ShoppingListDetails,
    val items: List<ShoppingListItem>
)
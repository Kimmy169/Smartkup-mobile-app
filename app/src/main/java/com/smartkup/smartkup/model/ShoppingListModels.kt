package com.smartkup.smartkup.model

import com.google.gson.annotations.SerializedName

data class ShoppingListDetails(
    @SerializedName("list_id", alternate = ["listId", "id"]) val listId: Long,
    @SerializedName("user_id", alternate = ["userId"]) val userId: Long?,
    val name: String,
    val status: String?
)

data class ShoppingListItem(
    @SerializedName("list_item_id", alternate = ["itemId", "id"]) val itemId: Long,
    @SerializedName("listId", alternate = ["list_id"]) val listId: Long,
    @SerializedName("productId", alternate = ["product_id"]) val productId: Long,
    @SerializedName("productName", alternate = ["product_name"]) val productName: String?,
    @SerializedName("categoryId", alternate = ["category_id"]) val categoryId: Long?,
    @SerializedName("productPrice", alternate = ["product_price"]) val productPrice: Double?,
    @SerializedName("shopId", alternate = ["shop_id"]) val shopId: Long?,
    val quantity: Double,
    val unit: String?,
    @SerializedName("purchased", alternate = ["is_purchased", "isPurchased"]) val purchased: Boolean
)

data class ShoppingListResponseDTO(
    val listDetails: ShoppingListDetails,
    val items: List<ShoppingListItem>
)
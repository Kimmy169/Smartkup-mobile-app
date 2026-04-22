package com.smartkup.smartkup.model

import com.google.gson.annotations.SerializedName

data class ShoppingListDetails(
    @SerializedName("list_id") val listId: Long,
    val userId: Long?,
    val name: String,
    val status: String?
)

data class ShoppingListItem(
    @SerializedName("list_item_id") val itemId: Long,
    val listId: Long,
    val productId: Long,
    val productName: String?,

    // ---> THIS IS THE MISSING PIECE THAT FIXES THE COMPILE ERROR <---
    val shopId: Long?,

    val quantity: Double,
    val unit: String?,
    @SerializedName("isPurchased") val purchased: Boolean
)

data class ShoppingListResponseDTO(
    val listDetails: ShoppingListDetails,
    val items: List<ShoppingListItem>
)
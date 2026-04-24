package com.smartkup.smartkup.network

import com.smartkup.smartkup.model.*
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query

interface SmartkupApi {

    @GET("api/categories")
    suspend fun getCategories(): Response<List<Category>>

    @GET("api/shops")
    suspend fun getShops(): Response<List<Shop>>

    @GET("api/products")
    suspend fun getProducts(): Response<List<Product>>

    @GET("api/categories/{categoryId}/pantry-items")
    suspend fun getPantryItemsByCategory(
        @Path("categoryId") categoryId: Long
    ): Response<List<PantryItem>>

    @GET("api/shopping-lists/{listId}")
    suspend fun getShoppingList(
        @Path("listId") listId: Long
    ): Response<ShoppingListResponseDTO>

    @POST("api/shopping-lists/items")
    suspend fun addItem(
        @Body item: ShoppingListItem
    ): Response<ShoppingListItem>

    @PUT("api/shopping-lists/items/{itemId}/toggle")
    suspend fun toggleItemStatus(
        @Path("itemId") itemId: Long,
        @Query("isPurchased") isPurchased: Boolean
    ): Response<Unit>

    @GET("api/shopping-lists")
    suspend fun getAllShoppingLists(): Response<List<ShoppingListDetails>>

    @POST("api/shopping-lists")
    suspend fun createShoppingList(@Body newList: ShoppingListDetails): Response<ShoppingListDetails>
}
package com.smartkup.smartkup.network

import com.smartkup.smartkup.model.Category
import com.smartkup.smartkup.model.ShoppingListItem
import com.smartkup.smartkup.model.ShoppingListResponseDTO
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query

interface SmartkupApi {
    // This tells Retrofit to call http://[YOUR_IP]:8080/api/categories
    @GET("api/categories")
    suspend fun getCategories(): Response<List<Category>>

    @GET("api/shopping-lists/{listId}")
    suspend fun getShoppingList(@Path("listId") listId: Long): Response<ShoppingListResponseDTO>

    @POST("api/shopping-lists/items")
    suspend fun addItem(@Body item: ShoppingListItem): Response<ShoppingListItem>

    @PUT("api/shopping-lists/items/{itemId}/toggle")
    suspend fun toggleItemStatus(
        @Path("itemId") itemId: Long,
        @Query("isPurchased") isPurchased: Boolean
    ): Response<Unit>
}
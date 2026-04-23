package com.smartkup.smartkup.repository

import android.util.Log
import com.smartkup.smartkup.model.Category
import com.smartkup.smartkup.model.Product
import com.smartkup.smartkup.model.Shop
import com.smartkup.smartkup.model.ShoppingListDetails
import com.smartkup.smartkup.model.ShoppingListItem
import com.smartkup.smartkup.model.ShoppingListResponseDTO
import com.smartkup.smartkup.network.SmartkupApi

class ShoppingListRepository(private val api: SmartkupApi) {

    suspend fun fetchShoppingList(listId: Long): ShoppingListResponseDTO? {
        return try {
            val response = api.getShoppingList(listId)
            if (response.isSuccessful) {
                response.body()
            } else {
                Log.e("ShoppingList", "Server Error: ${response.code()}")
                null
            }
        } catch (e: Exception) {
            Log.e("ShoppingList", "Network or Parsing Error: ${e.localizedMessage}")
            null
        }
    }

    suspend fun addItem(item: ShoppingListItem): Boolean {
        return try {
            val response = api.addItem(item)
            if (response.isSuccessful) {
                Log.d("ShoppingList", "Item added successfully!")
                true
            } else {
                Log.e("ShoppingList", "Failed to add item: ${response.code()}")
                false
            }
        } catch (e: Exception) {
            Log.e("ShoppingList", "Network error adding item: ${e.message}")
            false
        }
    }

    suspend fun toggleItemStatus(itemId: Long, isPurchased: Boolean): Boolean {
        return try {
            val response = api.toggleItemStatus(itemId, isPurchased)
            response.isSuccessful
        } catch (e: Exception) {
            false
        }
    }

    suspend fun fetchShops(): List<Shop> {
        return try {
            val response = api.getShops()
            if (response.isSuccessful) {
                Log.d("API_SUCCESS", "Shops fetched: ${response.body()?.size}")
                response.body() ?: emptyList()
            } else {
                Log.e("API_ERROR", "Failed to fetch shops. Server said: ${response.code()}")
                emptyList()
            }
        } catch (e: Exception) {
            Log.e("API_CRASH", "Retrofit crashed while fetching shops: ${e.message}")
            e.printStackTrace()
            emptyList()
        }
    }

    suspend fun fetchProducts(): List<Product> {
        return try {
            val response = api.getProducts()
            if (response.isSuccessful) response.body() ?: emptyList() else emptyList()
        } catch (e: Exception) { emptyList() }
    }

    suspend fun fetchAllLists(): List<ShoppingListDetails> {
        return try {
            val response = api.getAllShoppingLists()
            if (response.isSuccessful) response.body() ?: emptyList() else emptyList()
        } catch (e: Exception) { emptyList() }
    }

    suspend fun createList(name: String): Boolean {
        return try {
            // We pass 0L as ID, Spring Boot will generate the real ID automatically!
            val newList = ShoppingListDetails(listId = 0L, userId = 1L, name = name, status = "active")
            val response = api.createShoppingList(newList)
            response.isSuccessful
        } catch (e: Exception) { false }
    }

    suspend fun fetchCategories(): List<Category> {
        return try {
            val response = api.getCategories()
            if (response.isSuccessful) response.body() ?: emptyList() else emptyList()
        } catch (e: Exception) { emptyList() }
    }
}
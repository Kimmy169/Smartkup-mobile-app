package com.smartkup.smartkup.repository

import android.util.Log
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
            if (response.isSuccessful) {
                Log.d("ShoppingList", "Checkbox saved to database!")
                true
            } else {
                Log.e("ShoppingList", "Failed to save checkbox: ${response.code()}")
                false
            }
        } catch (e: Exception) {
            Log.e("ShoppingList", "Network error saving checkbox: ${e.message}")
            false
        }
    }
}
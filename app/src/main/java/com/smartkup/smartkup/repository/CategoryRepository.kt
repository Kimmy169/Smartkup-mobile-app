package com.smartkup.smartkup.repository

import android.util.Log
import com.smartkup.smartkup.network.SmartkupApi
import com.smartkup.smartkup.model.Category

class CategoryRepository(private val api: SmartkupApi) {

    suspend fun fetchCategories(): List<Category>? {
        return try {
            val response = api.getCategories()
            if (response.isSuccessful) {
                response.body()
            } else {
                Log.e("NETWORK_ERROR", "Server returned an error: ${response.code()}")
                null
            }
        } catch (e: Exception) {
            Log.e("NETWORK_ERROR", "Failed to connect: ${e.message}")
            null
        }
    }
}
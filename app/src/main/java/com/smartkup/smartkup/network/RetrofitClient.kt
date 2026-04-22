package com.smartkup.smartkup.network

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {
    // IMPORTANT: Replace this IP with the exact IP of the computer running Spring Boot.
    // If you are using the Android Emulator, and Spring Boot is on the same laptop, use "http://10.0.2.2:8080/"
    private const val BASE_URL = "http://192.168.1.211:8080/"

    val instance: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }
}
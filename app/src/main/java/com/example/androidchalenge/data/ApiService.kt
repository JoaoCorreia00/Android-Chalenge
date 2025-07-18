package com.example.androidchalenge.data

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

// API Data Classes
data class CatImage(
    val id: String,
    val url: String,
    val breeds: List<Breed>,
    var isFavorite: Boolean = false
)

data class Breed(
    val name: String,
    val id: String
)

// API Service Interface
interface CatApiService {
    @GET("images/search")
    suspend fun getCatImages(
        @Query("limit") limit: Int = 10,  // Fetch images
        @Query("has_breeds") hasBreeds: Int = 1,
        @Query("page") page: Int = 0,
        @Query("api_key") apiKey: String = "live_qny0T4AdsHpCgOBoqpbZNkiqEBjR3kqFz7TLbpS51gz6cjkFcyN0lIWqLwQQpqUv"
    ): List<CatImage>
}

// API Call Function
suspend fun fetchCatData(onResult: (List<CatImage>) -> Unit) {
    try {
        val retrofit = Retrofit.Builder()
            .baseUrl("https://api.thecatapi.com/v1/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val service = retrofit.create(CatApiService::class.java)
        val cats = service.getCatImages()

        withContext(Dispatchers.Main) {
            onResult(cats)
        }
    } catch (e: Exception) {
        withContext(Dispatchers.Main) {
            onResult(emptyList()) // Return an empty list on error
        }
    }
}
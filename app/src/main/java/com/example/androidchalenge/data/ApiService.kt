package com.example.androidchalenge.data

import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Path
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
    val life_span: String,
    val origin: String,
    val temperament: String,
    val description: String,
    val id: String
)

data class CatBreed(
    val id: String,
    val name: String
)

// API Service Interface
interface CatApiService {
    @GET("images/search")
    suspend fun getCatImages(
        @Query("limit") limit: Int = 10,
        @Query("has_breeds") hasBreeds: Int = 1,
        @Query("page") page: Int = 0,
        @Query("api_key") apiKey: String = "live_qny0T4AdsHpCgOBoqpbZNkiqEBjR3kqFz7TLbpS51gz6cjkFcyN0lIWqLwQQpqUv"
    ): List<CatImage>

    @GET("breeds")
    suspend fun getCatBreeds(): List<CatBreed>

    @GET("images/{id}")
    suspend fun getCatById(
        @Path("id") id: String,
        @Query("api_key") apiKey: String = "live_qny0T4AdsHpCgOBoqpbZNkiqEBjR3kqFz7TLbpS51gz6cjkFcyN0lIWqLwQQpqUv"
    ): CatImage

    @GET("images/search")
    suspend fun getCatImagesByBreedId(
        @Query("breed_ids") breedId: String,
        @Query("has_breeds") hasBreeds: Int = 1,
        @Query("page") page: Int = 0,
        @Query("limit") limit: Int = 10,
        @Query("api_key") apiKey: String = "live_qny0T4AdsHpCgOBoqpbZNkiqEBjR3kqFz7TLbpS51gz6cjkFcyN0lIWqLwQQpqUv"
    ): List<CatImage>
}

// API Call Functions
suspend fun fetchCatData(page: Int, limit: Int, onResult: (List<CatImage>) -> Unit) {
    try {
        val retrofit = Retrofit.Builder()
            .baseUrl("https://api.thecatapi.com/v1/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val service = retrofit.create(CatApiService::class.java)
        val cats = service.getCatImages(page = page, limit = limit)

        withContext(Dispatchers.Main) {
            onResult(cats)
        }
    } catch (e: Exception) {
        withContext(Dispatchers.Main) {
            onResult(emptyList())
        }
    }
}

suspend fun fetchCatBreeds(): List<CatBreed> {
    return withContext(Dispatchers.IO) {
        try {
            val retrofit = Retrofit.Builder()
                .baseUrl("https://api.thecatapi.com/v1/")
                .addConverterFactory(GsonConverterFactory.create())
                .build()
            val service = retrofit.create(CatApiService::class.java)
            service.getCatBreeds()
        } catch (e: Exception) {
            emptyList()
        }
    }
}

suspend fun fetchCatById(id: String, onResult: (CatImage?) -> Unit) {
    try {
        val retrofit = Retrofit.Builder()
            .baseUrl("https://api.thecatapi.com/v1/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val service = retrofit.create(CatApiService::class.java)
        val catImage = service.getCatById(id = id)

        withContext(Dispatchers.Main) {
            onResult(catImage)
        }
    } catch (e: Exception) {
        withContext(Dispatchers.Main) {
            onResult(null)
        }
    }
}

suspend fun fetchCatImagesByBreedId(breedId: String, page: Int, limit: Int, onResult: (List<CatImage>) -> Unit) {
    try {
        val retrofit = Retrofit.Builder()
            .baseUrl("https://api.thecatapi.com/v1/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val service = retrofit.create(CatApiService::class.java)
        val cats = service.getCatImagesByBreedId(breedId, page = page, limit = limit)

        withContext(Dispatchers.Main) {
            onResult(cats)
        }
    } catch (e: Exception) {
        withContext(Dispatchers.Main) {
            onResult(emptyList())
        }
    }
}

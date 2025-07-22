package com.example.androidchalenge.data

import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

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

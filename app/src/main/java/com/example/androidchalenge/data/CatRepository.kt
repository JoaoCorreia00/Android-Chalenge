package com.example.androidchalenge.data

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {
    private const val BASE_URL = "https://api.thecatapi.com/v1/"
    val catApiService: CatApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(CatApiService::class.java)
    }
}

class CatRepository(private val catDao: CatDao) {

    private val catApiService: CatApiService = RetrofitClient.catApiService

    suspend fun getCatImages(page: Int, limit: Int): List<CatImage> {
        return withContext(Dispatchers.IO) {
            try {
                catApiService.getCatImages(page = page, limit = limit)
            } catch (e: Exception) {
                emptyList()
            }
        }
    }

    suspend fun getCatBreeds(): List<CatBreed> {
        return withContext(Dispatchers.IO) {
            try {
                catApiService.getCatBreeds()
            } catch (e: Exception) {
                emptyList()
            }
        }
    }

    suspend fun getCatById(id: String): CatImage? {
        return withContext(Dispatchers.IO) {
            try {
                catApiService.getCatById(id = id)
            } catch (e: Exception) {
                null
            }
        }
    }

    suspend fun getCatImagesByBreedId(breedId: String, page: Int, limit: Int): List<CatImage> {
        return withContext(Dispatchers.IO) {
            try {
                catApiService.getCatImagesByBreedId(breedId, page = page, limit = limit)
            } catch (e: Exception) {
                emptyList()
            }
        }
    }

    val allFavorites: Flow<List<FavoriteCat>> = catDao.getAllFavorites()

    suspend fun addFavorite(cat: FavoriteCat) {
        catDao.insertFavorite(cat)
    }

    suspend fun removeFavorite(cat: FavoriteCat) {
        catDao.deleteFavorite(cat)
    }

    suspend fun isFavorite(catId: String): Boolean {
        return catDao.isFavorite(catId)
    }
}
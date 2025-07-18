package com.example.androidchalenge.data

import kotlinx.coroutines.flow.Flow

class CatRepository(private val catDao: CatDao) {
    val allFavorites: Flow<List<FavoriteCat>> = catDao.getAllFavorites()

    suspend fun addFavorite(cat: FavoriteCat) {
        catDao.insertFavorite(cat)
    }

    suspend fun removeFavorite(cat: FavoriteCat) {
        catDao.deleteFavorite(cat)
    }
}

package com.example.androidchalenge.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface CatDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFavorite(favorite: FavoriteCat)

    @Delete
    suspend fun deleteFavorite(favorite: FavoriteCat)

    @Query("SELECT * FROM favorite_cats")
    fun getAllFavorites(): Flow<List<FavoriteCat>>

    @Query("SELECT EXISTS(SELECT * FROM favorite_cats WHERE id = :catId)")
    suspend fun isFavorite(catId: String): Boolean
}
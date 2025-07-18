package com.example.androidchalenge.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "favorite_cats")
data class FavoriteCat(
    @PrimaryKey val id: String
)
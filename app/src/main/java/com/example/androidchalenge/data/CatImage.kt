package com.example.androidchalenge.data

data class CatImage(
    val id: String,
    val url: String,
    val breeds: List<Breed>,
    var isFavorite: Boolean = false // Default value for isFavorite
)
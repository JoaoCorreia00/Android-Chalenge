package com.example.androidchalenge.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.androidchalenge.data.CatRepository
import com.example.androidchalenge.data.FavoriteCat
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.*

class FavoriteViewModel(private val repository: CatRepository): ViewModel() {

    private val _favorites = MutableStateFlow<List<FavoriteCat>>(emptyList())
    val favorites: StateFlow<List<FavoriteCat>> = _favorites.asStateFlow()

    fun loadFavoriteCats() {
        viewModelScope.launch {
            repository.allFavorites.collect {
                _favorites.value = it
            }
        }
    }

    fun removeFavorite(favoriteCat: FavoriteCat) {
        viewModelScope.launch {
            repository.removeFavorite(favoriteCat)
        }
    }
}

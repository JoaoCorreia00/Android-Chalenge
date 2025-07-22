package com.example.androidchalenge.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.androidchalenge.data.CatImage
import com.example.androidchalenge.data.CatRepository
import com.example.androidchalenge.data.FavoriteCat
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class DetailViewModel(private val repository: CatRepository) : ViewModel() {

    private val _catImage = MutableStateFlow<CatImage?>(null)
    val catImage: StateFlow<CatImage?> = _catImage.asStateFlow()

    private val _loading = MutableStateFlow(true)
    val loading: StateFlow<Boolean> = _loading.asStateFlow()

    private val _isFavorite = MutableStateFlow(false)
    val isFavorite: StateFlow<Boolean> = _isFavorite.asStateFlow()

    fun fetchCatDetails(id: String) {
        viewModelScope.launch {
            _loading.value = true
            val cat = repository.getCatById(id)
            _catImage.value = cat
            _loading.value = false
            cat?.let {
                _isFavorite.value = repository.isFavorite(it.id)
            }
        }
    }

    fun toggleFavorite() {
        viewModelScope.launch {
            _catImage.value?.let { cat ->
                val breed = cat.breeds.firstOrNull()
                val favoriteCat = FavoriteCat(
                    id = cat.id,
                    url = cat.url,
                    name = breed?.name ?: "Unknown",
                    lifeSpan = breed?.life_span ?: "Unknown"
                )

                if (_isFavorite.value) {
                    repository.removeFavorite(favoriteCat)
                } else {
                    repository.addFavorite(favoriteCat)
                }
                _isFavorite.value = !_isFavorite.value
            }
        }
    }
}
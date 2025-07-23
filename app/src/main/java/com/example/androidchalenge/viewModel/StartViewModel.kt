package com.example.androidchalenge.viewModel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.androidchalenge.data.CatImage
import com.example.androidchalenge.data.CatBreed
import com.example.androidchalenge.data.CatRepository
import com.example.androidchalenge.data.FavoriteCat
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class StartViewModel(private val repository: CatRepository) : ViewModel() {

    private val _catImages = MutableStateFlow<List<CatImage>>(emptyList())
    val catImages: StateFlow<List<CatImage>> = _catImages.asStateFlow()

    private val _loading = MutableStateFlow(true)
    val loading: StateFlow<Boolean> = _loading.asStateFlow()

    private val _noResults = MutableStateFlow(false)
    val noResults: StateFlow<Boolean> = _noResults.asStateFlow()

    private val _currentPage = MutableStateFlow(0)
    val currentPage: StateFlow<Int> = _currentPage.asStateFlow()

    private val _currentBreedIds = MutableStateFlow<List<String>>(emptyList())
    val currentBreedIds: StateFlow<List<String>> = _currentBreedIds.asStateFlow()

    private val _breeds = MutableStateFlow<List<CatBreed>>(emptyList())
    val breeds: StateFlow<List<CatBreed>> = _breeds.asStateFlow()

    private val _filteredBreeds = MutableStateFlow<List<CatBreed>>(emptyList())
    val filteredBreeds: StateFlow<List<CatBreed>> = _filteredBreeds.asStateFlow()

    private val _showSuggestions = MutableStateFlow(false)
    val showSuggestions: StateFlow<Boolean> = _showSuggestions.asStateFlow()

    private val _searchText = MutableStateFlow("")
    val searchText: StateFlow<String> = _searchText.asStateFlow()

    private val itemsPerPage = 10

    init {
        viewModelScope.launch {
            _breeds.value = repository.getCatBreeds()
        }
    }

    suspend fun loadInitialCats() {
        _loading.value = true
        val initialCats = repository.getCatImages(page = _currentPage.value, limit = itemsPerPage)
        val favorites = repository.allFavorites.first()
        _catImages.value = initialCats.map { cat ->
            cat.copy(isFavorite = favorites.any { it.id == cat.id })
        }
        _loading.value = false
        _noResults.value = _catImages.value.isEmpty()
    }

    fun searchCats(query: String) {
        viewModelScope.launch {
            _breeds.value = repository.getCatBreeds()
            _loading.value = true
            _currentPage.value = 0
            _searchText.value = query
            _showSuggestions.value = false

            if (query.isBlank()) {
                _currentBreedIds.value = emptyList()
                loadInitialCats()
            } else {
                val breedNameList = query.split(",").map { it.trim() }
                val matchingBreedIds = _breeds.value.filter { breed ->
                    breedNameList.any { name -> breed.name.equals(name, ignoreCase = true) }
                }.map { it.id }

                if (matchingBreedIds.isNotEmpty()) {
                    _currentBreedIds.value = matchingBreedIds
                    val newCats = repository.getCatImagesByBreedId(
                        breedId = matchingBreedIds.joinToString(","),
                        page = _currentPage.value,
                        limit = itemsPerPage
                    )
                    val favorites = repository.allFavorites.first()
                    _catImages.value = newCats.map { cat ->
                        cat.copy(isFavorite = favorites.any { it.id == cat.id })
                    }
                    _noResults.value = _catImages.value.isEmpty()
                } else {
                    _catImages.value = emptyList()
                    _noResults.value = true
                    _currentBreedIds.value = emptyList()
                }
            }
            _loading.value = false
        }
    }

    fun loadMoreCats() {
        viewModelScope.launch {
            _currentPage.value++
            val newCats: List<CatImage> = if (_currentBreedIds.value.isNotEmpty()) {
                repository.getCatImagesByBreedId(
                    breedId = _currentBreedIds.value.joinToString(","),
                    page = _currentPage.value,
                    limit = itemsPerPage
                )
            } else {
                repository.getCatImages(page = _currentPage.value, limit = itemsPerPage)
            }
            val favorites = repository.allFavorites.first()
            val updatedNewCats = newCats.map { cat ->
                cat.copy(isFavorite = favorites.any { it.id == cat.id })
            }
            _catImages.value = _catImages.value + updatedNewCats
            _noResults.value = _catImages.value.isEmpty()
        }
    }

    fun onSearchTextChange(text: String) {
        _searchText.value = text
        val breedNameList = text.split(",").map { it.trim() }
        _filteredBreeds.value = _breeds.value.filter { breed ->
            breedNameList.any { name -> breed.name.contains(name, ignoreCase = true) }
        }
        _showSuggestions.value = text.isNotEmpty() && _filteredBreeds.value.isNotEmpty()
    }

    fun toggleFavorite(cat: CatImage, isFavorite: Boolean) {
        viewModelScope.launch {
            val breed = cat.breeds.firstOrNull()
            val favoriteCat = FavoriteCat(
                id = cat.id,
                url = cat.url,
                name = breed?.name ?: "Unknown",
                lifeSpan = breed?.life_span ?: "Unknown",
            )
            if (isFavorite) {
                repository.removeFavorite(favoriteCat)
            } else {
                repository.addFavorite(favoriteCat)
            }
            _catImages.value = _catImages.value.map {
                if (it.id == cat.id) it.copy(isFavorite = !isFavorite) else it
            }
        }
    }
}
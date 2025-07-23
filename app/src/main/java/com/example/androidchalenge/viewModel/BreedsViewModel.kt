package com.example.androidchalenge.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.androidchalenge.data.CatBreed
import com.example.androidchalenge.data.CatRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class BreedsViewModel(private val repository: CatRepository) : ViewModel() {

    private val _breeds = MutableStateFlow<List<CatBreed>>(emptyList())
    val breeds: StateFlow<List<CatBreed>> = _breeds.asStateFlow()

    private val _loading = MutableStateFlow(true)
    val loading: StateFlow<Boolean> = _loading.asStateFlow()

    fun fetchBreeds() {
        viewModelScope.launch {
            _loading.value = true
            _breeds.value = repository.getCatBreeds()
            _loading.value = false
        }
    }
}

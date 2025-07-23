package com.example.androidchalenge.viewModel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.example.androidchalenge.data.FavoriteCat
import com.example.androidchalenge.data.CatRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.*
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mockito.*

@ExperimentalCoroutinesApi
class FavoriteViewModelTest {

    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    private lateinit var viewModel: FavoriteViewModel
    private lateinit var repository: CatRepository

    @Before
    fun setup() {
        repository = mock(CatRepository::class.java)
        viewModel = FavoriteViewModel(repository)
        Dispatchers.setMain(UnconfinedTestDispatcher())
    }

    @Test
    fun `loadFavoriteCats should load favorites from repository`() = runTest {
        val favorites = listOf(FavoriteCat("Sia", "url1", "Siamese", "12 years"))
        `when`(repository.allFavorites).thenReturn(flowOf(favorites))

        viewModel.loadFavoriteCats()

        assertEquals(favorites, viewModel.favorites.value)
    }

    @Test
    fun `removeFavorite should call repository to remove favorite`() = runTest {
        val favoriteCat = FavoriteCat("Sia", "url1", "Siamese", "12 years")

        viewModel.removeFavorite(favoriteCat)

        verify(repository).removeFavorite(favoriteCat)
    }
}

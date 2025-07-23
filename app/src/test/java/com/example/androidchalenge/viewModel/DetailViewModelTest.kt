package com.example.androidchalenge.viewModel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.example.androidchalenge.data.CatImage
import com.example.androidchalenge.data.CatRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.*
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mockito.*

@ExperimentalCoroutinesApi
class DetailViewModelTest {

    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    private lateinit var viewModel: DetailViewModel
    private lateinit var repository: CatRepository

    @Before
    fun setup() {
        repository = mock(CatRepository::class.java)
        viewModel = DetailViewModel(repository)
        Dispatchers.setMain(UnconfinedTestDispatcher())
    }

    @Test
    fun `fetchCatDetails should load cat details and check if favorite`() = runTest {
        val catImage = CatImage("Sia", "url1", emptyList())
        `when`(repository.getCatById("Sia")).thenReturn(catImage)
        `when`(repository.isFavorite("Sia")).thenReturn(true)

        viewModel.fetchCatDetails("Sia")

        assertEquals(catImage, viewModel.catImage.value)
        assertEquals(true, viewModel.isFavorite.value)
    }
}

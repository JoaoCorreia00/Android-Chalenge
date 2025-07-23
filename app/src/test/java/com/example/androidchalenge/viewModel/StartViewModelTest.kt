package com.example.androidchalenge.viewModel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.example.androidchalenge.data.CatImage
import com.example.androidchalenge.data.CatBreed
import com.example.androidchalenge.data.Breed
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
class StartViewModelTest {

    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    private lateinit var viewModel: StartViewModel
    private lateinit var repository: CatRepository

    @Before
    fun setup() {
        repository = mock(CatRepository::class.java)
        viewModel = StartViewModel(repository)
        Dispatchers.setMain(UnconfinedTestDispatcher())
    }

    @Test
    fun `loadInitialCats should load cats`() = runTest {
        val breeds = listOf(Breed("Siamese", "12 years", "Asia", "Friendly", "A friendly breed", "1"))
        val catImages = listOf(CatImage("1", "url1", breeds))
        `when`(repository.getCatImages(0, 10)).thenReturn(catImages)
        `when`(repository.allFavorites).thenReturn(flowOf(emptyList()))

        viewModel.loadInitialCats()

        assertEquals(catImages, viewModel.catImages.value)
        assertEquals(false, viewModel.loading.value)
        assertEquals(false, viewModel.noResults.value)
    }

    @Test
    fun `searchCats should get catImages based on breed search`() = runTest {
        val breeds = listOf(Breed("Siamese", "12 years", "Asia", "Friendly", "A friendly breed", "Sia"))
        val catImages = listOf(CatImage("1", "url1", breeds))
        val catBreed = listOf(CatBreed("Sia", "Siamese"))
        `when`(repository.getCatBreeds()).thenReturn(catBreed)
        `when`(repository.getCatImagesByBreedId("Sia", 0, 10)).thenReturn(catImages)
        `when`(repository.allFavorites).thenReturn(flowOf(emptyList()))

        viewModel.searchCats("Siamese")

        assertEquals(catImages, viewModel.catImages.value)
        assertEquals(false, viewModel.noResults.value)
    }

    @Test
    fun `searchCats should set noResults to true when no matching breeds found`() = runTest {
        val catBreed = listOf(CatBreed("Siamese", "Siamese"))

        `when`(repository.getCatBreeds()).thenReturn(catBreed)
        `when`(repository.allFavorites).thenReturn(flowOf(emptyList()))

        viewModel.searchCats("Persian")

        assertEquals(emptyList<CatImage>(), viewModel.catImages.value)
        assertEquals(true, viewModel.noResults.value)
    }

    @Test
    fun `loadMoreCats should load more cats`() = runTest {
        val breeds = listOf(Breed("Siamese", "12 years", "Asia", "Friendly", "A friendly breed", "1"))
        val catImages = listOf(CatImage("1", "url1", breeds), CatImage("2", "url2", breeds))

        `when`(repository.getCatImages(1, 10)).thenReturn(catImages)
        `when`(repository.allFavorites).thenReturn(flowOf(emptyList()))

        viewModel.loadMoreCats()

        assertEquals(catImages, viewModel.catImages.value)
    }
}
package com.example.androidchalenge.viewModel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.example.androidchalenge.data.CatBreed
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
class BreedsViewModelTest {

    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    private lateinit var viewModel: BreedsViewModel
    private lateinit var repository: CatRepository

    @Before
    fun setup() {
        repository = mock(CatRepository::class.java)
        viewModel = BreedsViewModel(repository)
        Dispatchers.setMain(UnconfinedTestDispatcher())
    }

    @Test
    fun `fetchBreeds should load breeds from repository`() = runTest {
        val breeds = listOf(CatBreed("Sia", "Siamese"))
        `when`(repository.getCatBreeds()).thenReturn(breeds)

        viewModel.fetchBreeds()

        assertEquals(breeds, viewModel.breeds.value)
    }
}

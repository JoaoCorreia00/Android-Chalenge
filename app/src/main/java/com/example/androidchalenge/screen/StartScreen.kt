package com.example.androidchalenge.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

@Composable
fun StartScreen(modifier: Modifier = Modifier) {
    var catImages by remember { mutableStateOf<List<CatImage>>(emptyList()) }
    var loading by remember { mutableStateOf(true) }
    val scrollState = rememberScrollState()

    // Trigger API call when composable first appears
    LaunchedEffect(Unit) {
        fetchCatData { images ->
            catImages = images
            loading = false
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(scrollState),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Show loading indicator while fetching data
        if (loading) {
            Text(text = "Loading...", style = MaterialTheme.typography.bodyLarge)
        } else {
            // 2x2 Square Grid for Cat Information
            Column {
                catImages.chunked(2).forEach { rowImages ->
                    Row {
                        rowImages.forEach { cat ->
                            CatSquareWithInfo(cat)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun CatSquareWithInfo(cat: CatImage) {
    Column(
        modifier = Modifier.padding(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Square box
        Box(
            modifier = Modifier
                .size(150.dp)
                .background(MaterialTheme.colorScheme.secondary)
        )

        // API info string below square
        Text(
            text = "ID: ${cat.id}\nURL:\nName: ${cat.breeds}",
            modifier = Modifier.padding(top = 8.dp),
            style = MaterialTheme.typography.bodySmall
        )
    }
}

// API Data Classes
data class CatImage(
    val id: String,
    val url: String,
    val breeds: List<Breed> // Include breeds list
)

data class Breed(
    val name: String,
    val id: String
)

// API Service Interface
interface CatApiService {
    @GET("images/search")
    suspend fun getCatImages(
        @Query("limit") limit: Int = 4,  // Fetch 4 images for 2x2 grid
        @Query("has_breeds") breedIds: Int = 1,
        @Query("api_key") apiKey: String = "live_qny0T4AdsHpCgOBoqpbZNkiqEBjR3kqFz7TLbpS51gz6cjkFcyN0lIWqLwQQpqUv"
    ): List<CatImage>
}

// API Call Function
private suspend fun fetchCatData(onResult: (List<CatImage>) -> Unit) {
    try {
        val retrofit = Retrofit.Builder()
            .baseUrl("https://api.thecatapi.com/v1/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val service = retrofit.create(CatApiService::class.java)
        val cats = service.getCatImages()

        withContext(Dispatchers.Main) {
            onResult(cats)
        }
    } catch (e: Exception) {
        withContext(Dispatchers.Main) {
            onResult(emptyList()) // Return an empty list on error
        }
    }
}

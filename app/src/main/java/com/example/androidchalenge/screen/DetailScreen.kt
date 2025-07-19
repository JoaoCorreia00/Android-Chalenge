package com.example.androidchalenge.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import coil.compose.rememberAsyncImagePainter
import com.example.androidchalenge.data.CatImage
import com.example.androidchalenge.data.fetchCatById
import com.example.androidchalenge.data.AppDatabase
import com.example.androidchalenge.data.CatRepository
import com.example.androidchalenge.data.FavoriteCat
import com.example.androidchalenge.screen.ui.BottomNavBar
import kotlinx.coroutines.launch
import com.example.androidchalenge.R
import kotlinx.coroutines.flow.first

@Composable
fun DetailScreen(modifier: Modifier = Modifier, navController: NavHostController, id: String) {
    val context = LocalContext.current
    val database = AppDatabase.getDatabase(context)
    val repository = remember { CatRepository(database.catDao()) }
    val coroutineScope = rememberCoroutineScope()

    var catImage by remember { mutableStateOf<CatImage?>(null) }
    var loading by remember { mutableStateOf(true) }
    var isFavorite by remember { mutableStateOf(false) }

    LaunchedEffect(id) {
        fetchCatById(id) { result ->
            catImage = result
            loading = false

            result?.let { cat ->
                coroutineScope.launch {
                    val favorites = repository.allFavorites.first()
                    isFavorite = favorites.any { it.id == cat.id }
                }
            }
        }
    }

    Scaffold(
        bottomBar = { BottomNavBar(navController, "detail") }
    ) { paddingValues ->
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top
        ) {
            if (loading) {
                Text(text = "Loading...", style = MaterialTheme.typography.bodyLarge)
            } else {
                catImage?.let { cat ->
                    Box(
                        modifier = Modifier
                            .size(400.dp)
                            .padding(bottom = 16.dp)
                            .clip(RoundedCornerShape(8.dp))
                    ) {
                        Image(
                            painter = rememberAsyncImagePainter(cat.url),
                            contentDescription = "Cat Image",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )

                        val interactionSource = remember { MutableInteractionSource() }
                        Image(
                            painter = painterResource(
                                if (isFavorite) R.mipmap.stargoldfill
                                else R.mipmap.stargold
                            ),
                            contentDescription = "Toggle Favorite",
                            modifier = Modifier
                                .size(70.dp)
                                .align(Alignment.TopEnd)
                                .clickable(
                                    interactionSource = interactionSource,
                                    indication = null
                                ) {
                                    isFavorite = !isFavorite
                                    coroutineScope.launch {
                                        val breed = cat.breeds.firstOrNull()
                                        val favoriteCat = FavoriteCat(
                                            id = cat.id,
                                            url = cat.url,
                                            name = breed?.name ?: "Unknown",
                                            lifeSpan = breed?.life_span ?: "Unknown"
                                        )

                                        if (isFavorite) {
                                            repository.addFavorite(favoriteCat)
                                        } else {
                                            repository.removeFavorite(favoriteCat)
                                        }
                                    }
                                }
                                .padding(8.dp)
                        )
                    }

                    // Display cat information
                    cat.breeds.firstOrNull()?.let { breed ->
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(text = "Name: ${breed.name}", style = MaterialTheme.typography.titleLarge)
                            Text(text = "Origin: ${breed.origin}", style = MaterialTheme.typography.bodyMedium)
                            Text(text = "Temperament: ${breed.temperament}", style = MaterialTheme.typography.bodyMedium)
                            Text(text = "Description: ${breed.description}", style = MaterialTheme.typography.bodyMedium)
                        }
                    }
                } ?: run {
                    Text(text = "Cat not found", style = MaterialTheme.typography.bodyLarge)
                }
            }
        }
    }
}

package com.example.androidchalenge.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import coil.compose.rememberAsyncImagePainter
import com.example.androidchalenge.R
import com.example.androidchalenge.data.AppDatabase
import com.example.androidchalenge.data.Breed
import com.example.androidchalenge.data.CatImage
import com.example.androidchalenge.data.CatRepository
import com.example.androidchalenge.screen.ui.BottomNavBar
import kotlinx.coroutines.launch

@Composable
fun FavoriteScreen(modifier: Modifier = Modifier, navController: NavHostController) {
    val context = LocalContext.current
    val database = AppDatabase.getDatabase(context)
    val repository = remember { CatRepository(database.catDao()) }
    val favorites by repository.allFavorites.collectAsState(initial = emptyList())
    val coroutineScope = rememberCoroutineScope()
    val scrollState = rememberScrollState()

    Scaffold(
        bottomBar = { BottomNavBar(navController, "favorite") }
    ) { paddingValues ->
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(scrollState),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (favorites.isEmpty()) {
                Text(text = "No favorites yet", style = MaterialTheme.typography.bodyLarge)
            } else {
                Column {
                    favorites.chunked(2).forEach { rowFavorites ->
                        Row {
                            rowFavorites.forEach { favoriteCat ->
                                CatSquareWithInfo(
                                    cat = CatImage(
                                        id = favoriteCat.id,
                                        url = favoriteCat.url,
                                        breeds = listOf(
                                            Breed(
                                                name = favoriteCat.name,
                                                life_span = favoriteCat.lifeSpan,
                                                id = "",
                                                origin = "",
                                                temperament = "",
                                                description = ""
                                            )
                                        )
                                    ),
                                    onCatClick = {
                                        navController.navigate("detail/${favoriteCat.id}")
                                    },
                                    onStarClick = {
                                        coroutineScope.launch {
                                            // Remove from Room database
                                            repository.removeFavorite(favoriteCat)
                                        }
                                    }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun CatSquareWithInfo(
    cat: CatImage,
    onCatClick: () -> Unit,
    onStarClick: () -> Unit
) {
    Column(
        modifier = Modifier.padding(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(150.dp)
                .clickable(onClick = onCatClick)
        ) {
            // Cat image
            Image(
                painter = rememberAsyncImagePainter(cat.url),
                contentDescription = "Cat Image",
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )

            // Star in top-right corner
            Image(
                painter = painterResource(R.mipmap.stargoldfill),
                contentDescription = "Remove Favorite",
                modifier = Modifier
                    .size(40.dp)
                    .align(Alignment.TopEnd)
                    .clickable(
                        onClick = {
                            onStarClick()
                        },
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null
                    )
                    .padding(4.dp)
            )
        }

        // Breed name text
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            val breed = cat.breeds.firstOrNull()
            Text(
                text = breed?.name ?: "Unknown",
                modifier = Modifier.padding(top = 8.dp),
                style = MaterialTheme.typography.bodySmall
            )
            breed?.life_span?.let { lifespan ->
                val maxLifespan = lifespan.split(" - ").lastOrNull()
                Text(
                    text = "Lifespan: $maxLifespan years",
                    modifier = Modifier.padding(top = 1.dp),
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }
    }
}
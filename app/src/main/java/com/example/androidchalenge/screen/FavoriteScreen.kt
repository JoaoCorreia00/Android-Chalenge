package com.example.androidchalenge.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavHostController
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.androidchalenge.data.AppDatabase
import com.example.androidchalenge.data.Breed
import com.example.androidchalenge.data.CatImage
import com.example.androidchalenge.data.CatRepository
import com.example.androidchalenge.data.FavoriteCat
import com.example.androidchalenge.screen.ui.BottomNavBar
import com.example.androidchalenge.screen.ui.CatSquareWithInfo
import com.example.androidchalenge.viewModel.FavoriteViewModel
import com.example.androidchalenge.viewModel.ViewModelFactory

@Composable
fun FavoriteScreen(modifier: Modifier = Modifier, navController: NavHostController) {
    val context = LocalContext.current
    val repository = remember { CatRepository(AppDatabase.getDatabase(context).catDao()) }
    val viewModel: FavoriteViewModel = viewModel(factory = ViewModelFactory(repository))

    val favorites by viewModel.favorites.collectAsState()

    LaunchedEffect("") {
        viewModel.loadFavoriteCats()
    }

    Scaffold(
        bottomBar = { BottomNavBar(navController, "favorite") }
    ) { paddingValues ->
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState()),
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
                                    cat = favoriteCat.toCatImage(), // Convert FavoriteCat to CatImage
                                    onCatClick = {
                                        navController.navigate("detail/${favoriteCat.id}")
                                    },
                                    onStarClick = { isFavorite ->
                                        viewModel.removeFavorite(favoriteCat)
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
fun FavoriteCat.toCatImage(): CatImage {
    return CatImage(
        id = this.id,
        url = this.url,
        breeds = listOf(Breed(name = this.name, life_span = this.lifeSpan, id = "", origin = "", temperament = "", description = "")),
        isFavorite = true  // Set to true since this is a favorite
    )
}
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import coil.compose.rememberAsyncImagePainter
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.androidchalenge.R
import com.example.androidchalenge.data.AppDatabase
import com.example.androidchalenge.data.CatRepository
import com.example.androidchalenge.screen.ui.BottomNavBar
import com.example.androidchalenge.viewModel.DetailViewModel
import com.example.androidchalenge.viewModel.ViewModelFactory

@Composable
fun DetailScreen(modifier: Modifier = Modifier, navController: NavHostController, id: String) {
    val context = LocalContext.current
    val repository = remember { CatRepository(AppDatabase.getDatabase(context).catDao()) }
    val viewModel: DetailViewModel = viewModel(factory = ViewModelFactory(repository))

    val catImage by viewModel.catImage.collectAsState()
    val loading by viewModel.loading.collectAsState()
    val isFavorite by viewModel.isFavorite.collectAsState()

    LaunchedEffect(id) {
        viewModel.fetchCatDetails(id)
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
                                    viewModel.toggleFavorite()
                                }
                                .padding(8.dp)
                        )
                    }

                    cat.breeds.firstOrNull()?.let { breed ->
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(text = breed.name, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleLarge)
                            Text(text = breed.origin, style = MaterialTheme.typography.bodyMedium)
                            Spacer(modifier = Modifier.height(12.dp))
                            Text(text = breed.description, style = MaterialTheme.typography.bodyMedium)
                            Spacer(modifier = Modifier.height(12.dp))
                            Text(text = "Temperament: ${breed.temperament}", style = MaterialTheme.typography.bodyMedium)
                        }
                    }
                } ?: run {
                    Text(text = "Cat not found", style = MaterialTheme.typography.bodyLarge)
                }
            }
        }
    }
}
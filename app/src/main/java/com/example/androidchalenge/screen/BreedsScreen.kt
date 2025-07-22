package com.example.androidchalenge.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.androidchalenge.data.AppDatabase
import com.example.androidchalenge.data.CatBreed
import com.example.androidchalenge.data.CatRepository
import com.example.androidchalenge.screen.ui.BottomNavBar
import com.example.androidchalenge.viewModel.BreedsViewModel
import com.example.androidchalenge.viewModel.ViewModelFactory

@Composable
fun BreedsScreen(modifier: Modifier = Modifier, navController: NavHostController) {
    val context = LocalContext.current
    val repository = remember { CatRepository(AppDatabase.getDatabase(context).catDao()) }
    val viewModel: BreedsViewModel = viewModel(factory = ViewModelFactory(repository))

    val breeds by viewModel.breeds.collectAsState()
    val loading by viewModel.loading.collectAsState()
    val scrollState = rememberScrollState()

    Scaffold(
        bottomBar = { BottomNavBar(navController, "breed") }
    ) { paddingValues ->
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(scrollState),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (loading) {
                Text(text = "Loading...", style = MaterialTheme.typography.bodyLarge)
            } else {
                Column {
                    breeds.chunked(2).forEach { rowBreeds ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.Center
                        ) {
                            rowBreeds.forEach { breed ->
                                BreedBox(
                                    breed = breed,
                                    onBreedClick = {
                                        navController.navigate("start?breed=${breed.name}")
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
fun BreedBox(
    breed: CatBreed,
    onBreedClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .padding(8.dp)
            .width(145.dp)
            .height(52.dp)
            .clickable(onClick = onBreedClick)
            .background(MaterialTheme.colorScheme.surface, shape = RoundedCornerShape(16.dp))
            .border(1.dp, MaterialTheme.colorScheme.onSurface, shape = RoundedCornerShape(16.dp)),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = breed.name,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.padding(horizontal = 8.dp),
            textAlign = TextAlign.Center
        )
    }
}
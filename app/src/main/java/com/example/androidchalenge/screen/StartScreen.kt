    package com.example.androidchalenge.screen

    import androidx.compose.foundation.border
    import androidx.compose.foundation.layout.*
    import androidx.compose.foundation.rememberScrollState
    import androidx.compose.foundation.shape.RoundedCornerShape
    import androidx.compose.foundation.verticalScroll
    import androidx.compose.material3.*
    import androidx.compose.runtime.*
    import androidx.compose.ui.Alignment
    import androidx.compose.ui.Modifier
    import androidx.compose.ui.platform.LocalContext
    import androidx.compose.ui.unit.dp
    import androidx.navigation.NavHostController
    import androidx.lifecycle.viewmodel.compose.viewModel
    import com.example.androidchalenge.data.AppDatabase
    import com.example.androidchalenge.data.CatRepository
    import com.example.androidchalenge.screen.ui.BottomNavBar
    import com.example.androidchalenge.screen.ui.CatSquareWithInfo
    import com.example.androidchalenge.screen.ui.SearchBar
    import com.example.androidchalenge.viewModel.StartViewModel
    import com.example.androidchalenge.viewModel.ViewModelFactory

    @Composable
    fun StartScreen(modifier: Modifier = Modifier, navController: NavHostController, breedName: String? = null) {
        val context = LocalContext.current
        val repository = remember { CatRepository(AppDatabase.getDatabase(context).catDao()) }
        val viewModel: StartViewModel = viewModel(factory = ViewModelFactory(repository))

        // Observe states from ViewModel
        val catImages by viewModel.catImages.collectAsState()
        val loading by viewModel.loading.collectAsState()
        val noResults by viewModel.noResults.collectAsState()
        val searchText by viewModel.searchText.collectAsState()
        val filteredBreeds by viewModel.filteredBreeds.collectAsState()
        val showSuggestions by viewModel.showSuggestions.collectAsState()

        LaunchedEffect(breedName) {
            if (breedName != null) {
                viewModel.searchCats(breedName)
            }
            else{
                viewModel.loadInitialCats()
            }
        }

        Scaffold(
            topBar = {
                SearchBar(
                    searchText = searchText,
                    onSearchTextChange = viewModel::onSearchTextChange,
                    onSearch = viewModel::searchCats,
                    filteredBreeds = filteredBreeds,
                    showSuggestions = showSuggestions
                )
            },
            bottomBar = { BottomNavBar(navController, "start") }
        ) { paddingValues ->
            Column(
                modifier = modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                if (loading) {
                    Text(text = "Loading...", style = MaterialTheme.typography.bodyLarge)
                } else if (noResults) {
                    Text(text = "No cats found for the given breed.", style = MaterialTheme.typography.bodyLarge)
                } else {
                    Column {
                        catImages.chunked(2).forEach { rowImages ->
                            Row {
                                rowImages.forEach { cat ->
                                    CatSquareWithInfo(
                                        cat = cat,
                                        onCatClick = {
                                            navController.navigate("detail/${cat.id}")
                                        },
                                        onStarClick = { isFavorite ->
                                            viewModel.toggleFavorite(cat, isFavorite)
                                        }
                                    )
                                }
                            }
                        }
                        Button(
                            onClick = viewModel::loadMoreCats,
                            modifier = Modifier
                                .padding(16.dp)
                                .align(Alignment.CenterHorizontally)
                                .border(1.dp, MaterialTheme.colorScheme.onSurface, RoundedCornerShape(8.dp)),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.surface
                            )
                        ) {
                            Text(
                                text = "Load More",
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                }
            }
        }
    }
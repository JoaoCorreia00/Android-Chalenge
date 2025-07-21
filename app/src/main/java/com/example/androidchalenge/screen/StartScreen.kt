import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import coil.compose.rememberAsyncImagePainter
import com.example.androidchalenge.R
import com.example.androidchalenge.data.CatImage
import com.example.androidchalenge.data.fetchCatData
import com.example.androidchalenge.data.FavoriteCat
import com.example.androidchalenge.data.AppDatabase
import com.example.androidchalenge.data.CatBreed
import com.example.androidchalenge.data.CatRepository
import com.example.androidchalenge.data.fetchCatBreeds
import com.example.androidchalenge.data.fetchCatImagesByBreedId
import com.example.androidchalenge.screen.ui.BottomNavBar
import kotlinx.coroutines.launch

@Composable
fun StartScreen(modifier: Modifier = Modifier, navController: NavHostController, breedName: String? = null) {
    val context = LocalContext.current
    val database = AppDatabase.getDatabase(context)
    val repository = remember { CatRepository(database.catDao()) }
    val favorites by repository.allFavorites.collectAsState(initial = emptyList())
    val coroutineScope = rememberCoroutineScope()

    var catImages by remember { mutableStateOf<List<CatImage>>(emptyList()) }
    var loading by remember { mutableStateOf(true) }
    var currentPage by remember { mutableIntStateOf(0) }
    var noResults by remember { mutableStateOf(false) }
    var currentBreedIds by remember { mutableStateOf<List<String>>(emptyList()) }
    var breeds by remember { mutableStateOf<List<CatBreed>>(emptyList()) }
    var filteredBreeds by remember { mutableStateOf<List<CatBreed>>(emptyList()) }
    var showSuggestions by remember { mutableStateOf(false) }
    var searchText by remember { mutableStateOf(breedName ?: "") }
    val itemsPerPage = 10
    val scrollState = rememberScrollState()

    LaunchedEffect(Unit) {
        breeds = fetchCatBreeds()
        loadCats(currentPage, itemsPerPage) { images ->
            catImages = images.map { cat ->
                cat.copy(isFavorite = favorites.any { it.id == cat.id })
            }
            loading = false
            noResults = catImages.isEmpty()
        }

        if (!searchText.isBlank()) {
            val breedNameList = searchText.split(",").map { it.trim() }
            val breedIds = breeds.filter { breed ->
                breedNameList.any { name -> breed.name.equals(name, ignoreCase = true) }
            }.map { it.id }

            if (breedIds.isNotEmpty()) {
                currentBreedIds = breedIds
                // Fetch cats by breed IDs
                loadCatsByBreedIds(breedIds, currentPage, itemsPerPage) { images ->
                    catImages = images.map { cat ->
                        cat.copy(isFavorite = favorites.any { it.id == cat.id })
                    }
                    loading = false
                    noResults = catImages.isEmpty()
                }
            } else {
                catImages = emptyList()
                loading = false
                noResults = true
                currentBreedIds = emptyList()
            }
        }
    }

    // Use a Scaffold to manage the layout
    Scaffold(
        topBar = {
            // Search Bar
            SearchBar(
                onSearch = { breedNames ->
                    coroutineScope.launch {
                        showSuggestions = false
                        if (breedNames.isBlank()) {
                            loadCats(currentPage, itemsPerPage) { images ->
                                catImages = images.map { cat ->
                                    cat.copy(isFavorite = favorites.any { it.id == cat.id })
                                }
                                loading = false
                                noResults = catImages.isEmpty()
                                currentBreedIds = emptyList()
                            }
                        } else {
                            val breedNameList = breedNames.split(",").map { it.trim() }
                            val breedIds = breeds.filter { breed ->
                                breedNameList.any { name -> breed.name.equals(name, ignoreCase = true) }
                            }.map { it.id }

                            if (breedIds.isNotEmpty()) {
                                currentBreedIds = breedIds
                                // Fetch cats by breed IDs
                                loadCatsByBreedIds(breedIds, currentPage, itemsPerPage) { images ->
                                    catImages = images.map { cat ->
                                        cat.copy(isFavorite = favorites.any { it.id == cat.id })
                                    }
                                    loading = false
                                    noResults = catImages.isEmpty()
                                }
                            } else {
                                catImages = emptyList()
                                loading = false
                                noResults = true
                                currentBreedIds = emptyList()
                            }
                        }
                    }
                },
                onTextChange = { text ->
                    searchText = text
                    val breedNameList = text.split(",").map { it.trim() }
                    filteredBreeds = breeds.filter { breed ->
                        breedNameList.any { name -> breed.name.contains(name, ignoreCase = true) }
                    }
                    showSuggestions = text.isNotEmpty() && filteredBreeds.isNotEmpty()
                },
                filteredBreeds = filteredBreeds,
                showSuggestions = showSuggestions,
                searchText = searchText
            )
        },
        bottomBar = { BottomNavBar(navController, "start") } // Use the new BottomNavBar
    ) { paddingValues ->
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(paddingValues) // Add padding to avoid overlap with the bottom bar
                .verticalScroll(scrollState),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Show loading indicator while fetching data
            if (loading) {
                Text(text = "Loading...", style = MaterialTheme.typography.bodyLarge)
            } else if (noResults) {
                Text(text = "No cats found for the given breed.", style = MaterialTheme.typography.bodyLarge)
            } else {
                // 2x2 Square Grid for Cat Information
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
                                        coroutineScope.launch {
                                            val breed = cat.breeds.firstOrNull()
                                            val favoriteCat = FavoriteCat(
                                                id = cat.id,
                                                url = cat.url,
                                                name = breed?.name ?: "Unknown",
                                                lifeSpan = breed?.life_span ?: "Unknown",
                                            )

                                            if (isFavorite) {
                                                repository.addFavorite(favoriteCat) // Add to favorites
                                            } else {
                                                repository.removeFavorite(favoriteCat) // Remove from favorites
                                            }
                                        }
                                    }
                                )
                            }
                        }
                    }
                    // Load Button
                    Button(
                        onClick = {
                            coroutineScope.launch {
                                currentPage += 1
                                if (currentBreedIds.isNotEmpty()) {
                                    // Load more cats by the current breed IDs
                                    loadCatsByBreedIds(currentBreedIds, currentPage, itemsPerPage) { newImages ->
                                        catImages = catImages + newImages.map { cat ->
                                            cat.copy(isFavorite = favorites.any { it.id == cat.id })
                                        }
                                        noResults = catImages.isEmpty() // Check if there are no results
                                    }
                                } else {
                                    // Load more cats normally
                                    loadCats(currentPage, itemsPerPage) { newImages ->
                                        catImages = catImages + newImages.map { cat ->
                                            cat.copy(isFavorite = favorites.any { it.id == cat.id })
                                        }
                                        noResults = catImages.isEmpty()
                                    }
                                }
                            }
                        },
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

@Composable
fun SearchBar(
    onSearch: (String) -> Unit,
    onTextChange: (String) -> Unit,
    filteredBreeds: List<CatBreed>,
    showSuggestions: Boolean,
    searchText: String
) {
    var localSearchText by remember { mutableStateOf(searchText) }

    Column {
        TextField(
            value = localSearchText,
            onValueChange = {
                localSearchText = it
                onTextChange(it)
            },
            placeholder = {
                Text(
                    text = "Search for cat breeds (e.g., Persian, Bengal)",
                    style = TextStyle(
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Normal
                    )
                )
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            colors = TextFieldDefaults.colors(
                focusedContainerColor = MaterialTheme.colorScheme.surface,
                unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                focusedTextColor = MaterialTheme.colorScheme.onSurface,
                unfocusedTextColor = MaterialTheme.colorScheme.onSurface,
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent
            ),
            singleLine = true,
            shape = RoundedCornerShape(16.dp),
            trailingIcon = {
                IconButton(onClick = { onSearch(localSearchText) }) {
                    Icon(imageVector = Icons.Default.Search, contentDescription = "Search")
                }
            }
        )

        if (showSuggestions && filteredBreeds.isNotEmpty()) {
            Column(modifier = Modifier.fillMaxWidth()) {
                filteredBreeds.forEach { breed ->
                    Text(
                        text = breed.name,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                val lastCommaIndex = localSearchText.lastIndexOf(",")
                                localSearchText = if (lastCommaIndex != -1) {
                                    localSearchText.substring(0, lastCommaIndex) + ", ${breed.name}"
                                } else {
                                    breed.name
                                }
                                onTextChange(localSearchText)
                                onSearch(localSearchText)
                            }
                            .padding(8.dp),
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        }
    }
}

@Composable
fun CatSquareWithInfo(
    cat: CatImage,
    onCatClick: () -> Unit,
    onStarClick: (Boolean) -> Unit
) {
    var isFavorite by remember { mutableStateOf(cat.isFavorite) }

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

            // star in top-right corner
            Image(
                painter = painterResource(
                    if (isFavorite) R.mipmap.stargoldfill
                    else R.mipmap.stargold
                ),
                contentDescription = "Toggle Favorite",
                modifier = Modifier
                    .size(40.dp)
                    .align(Alignment.TopEnd)
                    .clickable(
                        onClick = {
                            isFavorite = !isFavorite
                            onStarClick(isFavorite)
                        },
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null
                    )
                    .padding(4.dp)
            )
        }

        // Breed name text
        Text(
            text = cat.breeds.firstOrNull()?.name ?: "Unknown",
            modifier = Modifier.padding(top = 8.dp),
            style = MaterialTheme.typography.bodySmall
        )
    }
}

private suspend fun loadCats(page: Int, limit: Int, onResult: (List<CatImage>) -> Unit) {
    fetchCatData(page, limit) { images ->
        onResult(images)
    }
}

private suspend fun loadCatsByBreedIds(breedIds: List<String>, page: Int, limit: Int, onResult: (List<CatImage>) -> Unit) {
    val breedIdString = breedIds.joinToString(",")
    fetchCatImagesByBreedId(breedIdString, page, limit) { images ->
        onResult(images)
    }
}
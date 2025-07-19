import androidx.compose.foundation.Image
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
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import coil.compose.rememberAsyncImagePainter
import com.example.androidchalenge.R
import com.example.androidchalenge.data.CatImage
import com.example.androidchalenge.data.fetchCatData
import com.example.androidchalenge.data.FavoriteCat
import com.example.androidchalenge.data.AppDatabase
import com.example.androidchalenge.data.CatRepository
import com.example.androidchalenge.screen.ui.BottomNavBar
import kotlinx.coroutines.launch

@Composable
fun StartScreen(modifier: Modifier = Modifier, navController: NavHostController) {
    val context = LocalContext.current
    val database = AppDatabase.getDatabase(context)
    val repository = remember { CatRepository(database.catDao()) }
    val favorites by repository.allFavorites.collectAsState(initial = emptyList())
    val coroutineScope = rememberCoroutineScope()

    var catImages by remember { mutableStateOf<List<CatImage>>(emptyList()) }
    var loading by remember { mutableStateOf(true) }
    val scrollState = rememberScrollState()

    // Trigger API call when composable first appears
    LaunchedEffect(Unit) {
        fetchCatData { images ->
            catImages = images.map { cat ->
                cat.copy(isFavorite = favorites.any { it.id == cat.id })
            }
            loading = false
        }
    }

    // Use a Scaffold to manage the layout
    Scaffold(
        topBar = {
            // Search Bar
            SearchBar()
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
                }
            }
        }
    }
}

@Composable
fun SearchBar() {
    var searchText by remember { mutableStateOf("") }

    TextField(
        value = searchText,
        onValueChange = { searchText = it },
        placeholder = { Text("Search for cats...") },
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
        leadingIcon = {
            Icon(
                imageVector = Icons.Default.Search,
                contentDescription = "Search"
            )
        }
    )
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
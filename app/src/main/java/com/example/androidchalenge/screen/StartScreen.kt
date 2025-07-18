import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import coil.compose.rememberImagePainter
import com.example.androidchalenge.R
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

@Composable
fun StartScreen(modifier: Modifier = Modifier, navController: NavHostController) {
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

    // Use a Scaffold to manage the layout
    Scaffold(
        bottomBar = { BottomNavBar(navController) }
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
                                CatSquareWithInfo(cat)
                            }
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
        // Load the cat image from the URL
        Image(
            painter = rememberImagePainter(cat.url),
            contentDescription = "Cat Image",
            modifier = Modifier
                .size(150.dp)
                .background(MaterialTheme.colorScheme.secondary)
        )

        // API info string below the image
        Text(
            text = "ID: ${cat.id}\nName: ${cat.breeds.firstOrNull()?.name ?: "Unknown"}",
            modifier = Modifier.padding(top = 8.dp),
            style = MaterialTheme.typography.bodySmall
        )
    }
}

@Composable
fun BottomNavBar(navController: NavHostController) {
    BottomAppBar {
        NavigationBar {
            NavigationBarItem(
                selected = false,
                onClick = { navController.navigate("start") },
                icon = { Image(
                    painter = painterResource(R.mipmap.homefill),
                    contentDescription = "Home",
                    modifier = Modifier.size(22.dp)
                ) },
                label = { Text("Home") }
            )
            NavigationBarItem(
                selected = false,
                onClick = { navController.navigate("favorite") },
                icon = { Image(
                    painter = painterResource(R.mipmap.star),
                    contentDescription = "Favorites",
                    modifier = Modifier.size(22.dp)
                ) },
                label = { Text("Favorites") }
            )
        }
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
        @Query("limit") limit: Int = 8,  // Fetch images
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

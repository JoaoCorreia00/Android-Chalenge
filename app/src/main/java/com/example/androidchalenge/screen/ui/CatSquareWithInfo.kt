package com.example.androidchalenge.screen.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import com.example.androidchalenge.R
import com.example.androidchalenge.data.CatImage
import androidx.compose.material3.Text
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember

@Composable
fun CatSquareWithInfo(
    cat: CatImage,
    onCatClick: () -> Unit,
    onStarClick: (Boolean) -> Unit
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
                painter = painterResource(
                    if (cat.isFavorite) R.mipmap.stargoldfill
                    else R.mipmap.stargold
                ),
                contentDescription = "Toggle Favorite",
                modifier = Modifier
                    .size(40.dp)
                    .align(Alignment.TopEnd)
                    .clickable(
                        onClick = {
                            onStarClick(cat.isFavorite) // Pass the current favorite state
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
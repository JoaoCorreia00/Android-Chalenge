package com.example.androidchalenge.screen.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.androidchalenge.data.CatBreed

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchBar(
    searchText: String,
    onSearchTextChange: (String) -> Unit,
    onSearch: (String) -> Unit,
    filteredBreeds: List<CatBreed>,
    showSuggestions: Boolean
) {
    Column {
        TextField(
            value = searchText,
            onValueChange = {
                onSearchTextChange(it)
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
                IconButton(onClick = { onSearch(searchText) }) {
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
                                val lastCommaIndex = searchText.lastIndexOf(",")
                                val newSearchText = if (lastCommaIndex != -1) {
                                    searchText.substring(0, lastCommaIndex) + ", ${breed.name}"
                                } else {
                                    breed.name
                                }
                                onSearchTextChange(newSearchText)
                                onSearch(newSearchText)
                            }
                            .padding(8.dp),
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        }
    }
}
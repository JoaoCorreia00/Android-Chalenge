package com.example.androidchalenge.screen

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.androidchalenge.screen.ui.BottomNavBar

@Composable
fun DetailScreen(modifier: Modifier = Modifier, navController: NavHostController, id: String) {

    Scaffold(
        bottomBar = { BottomNavBar(navController, "detail") }
    ) { paddingValues ->
        // Your detail screen content
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            Text(
                text = "Detail Screen for Cat $id",
                modifier = Modifier.padding(16.dp)
            )
            // Add your detailed cat information here
        }
    }
}
package com.example.androidchalenge.screen.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.size
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.androidchalenge.R

@Composable
fun BottomNavBar(navController: NavHostController, currentRoute: String?) {
    BottomAppBar {
        NavigationBar {
            NavigationBarItem(
                selected = false, // Set selected to false to avoid the border
                onClick = { navController.navigate("start") },
                icon = {
                    Image(
                        painter = painterResource(
                            if (currentRoute == "start") R.mipmap.homefill else R.mipmap.home
                        ),
                        contentDescription = "Home",
                        modifier = Modifier.size(22.dp)
                    )
                },
                label = { Text("Home") },
            )
            NavigationBarItem(
                selected = false,
                onClick = { navController.navigate("breed") },
                icon = {
                    Image(
                        painter = painterResource(
                            if (currentRoute == "breed") R.mipmap.petsfill else R.mipmap.pets
                        ),
                        contentDescription = "Breeds",
                        modifier = Modifier.size(22.dp)
                    )
                },
                label = { Text("Breeds") },
            )
            NavigationBarItem(
                selected = false,
                onClick = { navController.navigate("favorite") },
                icon = {
                    Image(
                        painter = painterResource(
                            if (currentRoute == "favorite") R.mipmap.starfill else R.mipmap.star
                        ),
                        contentDescription = "Favorites",
                        modifier = Modifier.size(22.dp)
                    )
                },
                label = { Text("Favorites") },

            )
        }
    }
}



package com.example.androidchalenge

import StartScreen
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.androidchalenge.screen.BreedsScreen
import com.example.androidchalenge.screen.DetailScreen
import com.example.androidchalenge.screen.FavoriteScreen

@Composable
fun AppNavigation(modifier: Modifier = Modifier) {

    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = "start") {

        composable("start"){
            StartScreen(modifier, navController)
        }

        composable("detail/{Id}"){
            var id = it.arguments?.getString("Id")
            DetailScreen(modifier,navController,id?:"")
        }

        composable("favorite"){
            FavoriteScreen(modifier,navController)
        }

        composable("breed"){
            BreedsScreen(modifier,navController)
        }

    }
}
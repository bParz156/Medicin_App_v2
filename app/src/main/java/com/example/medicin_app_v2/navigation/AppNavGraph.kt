package com.example.medicin_app_v2.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.medicin_app_v2.ui.home.HomeScreen

//import androidx.navigation.NavHostController

@Composable
fun MedicinNavHost(
    navController: NavHostController,
    modifier: Modifier = Modifier,
) {

    NavHost(
        navController = navController,
        startDestination = Location.HOME.name,
        modifier = modifier
    ) {
        composable(route = Location.HOME.name) {
            HomeScreen()
        }

    }


}
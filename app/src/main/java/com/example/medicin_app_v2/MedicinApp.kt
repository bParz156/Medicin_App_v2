package com.example.medicin_app_v2

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.medicin_app_v2.navigation.MedicinNavHost
import com.example.medicin_app_v2.ui.home.HomeScreen

@Composable
fun MedicinApp(navController: NavHostController = rememberNavController()) {
    MedicinNavHost(navController = navController)
    //HomeScreen()
}

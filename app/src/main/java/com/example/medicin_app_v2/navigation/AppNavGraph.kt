package com.example.medicin_app_v2.navigation

import android.util.Log
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.medicin_app_v2.ui.CommunUI
import com.example.medicin_app_v2.ui.home.HomeDestination
import com.example.medicin_app_v2.ui.home.HomeScreen
import com.example.medicin_app_v2.ui.magazyn.MagazynDestination
import com.example.medicin_app_v2.ui.magazyn.MagazynScreen
import com.example.medicin_app_v2.ui.patients.PatientsDestination
import com.example.medicin_app_v2.ui.patients.PatientsListScreen
import com.example.medicin_app_v2.ui.powiadomienia.PowiadomieniaDestination
import com.example.medicin_app_v2.ui.powiadomienia.PowiadomieniaScreen
import com.example.medicin_app_v2.ui.ustawienia.UstawieniaDestination
import com.example.medicin_app_v2.ui.ustawienia.UstawieniaScreen
import com.example.medicin_app_v2.ui.zalecenia.ZaleceniaDestination
import com.example.medicin_app_v2.ui.zalecenia.ZaleceniaScreen

/**
 * Funkcja odpowiedzialna za nawigację aplikacji, nawigowanie ma prametr, których jest id pacjenta
 */
@Composable
fun MedicinNavHost(
    patientId: Int,
    navController: NavHostController,
    modifier: Modifier = Modifier,
) {
    val id=patientId


    NavHost(
        navController = navController,
        startDestination = "${HomeDestination.route}/$id",
      //  startDestination = HomeDestination.route,
        modifier = modifier
    ) {


        composable(route = HomeDestination.routeWithArgs, arguments = listOf(navArgument(HomeDestination.patientIdArg){
            type = NavType.IntType
        })) {
            Log.i("patientdId", "z AppNavGraph $id")
            HomeScreen(
                onButtonHomeClick = {},
                onButtonMagazynClicked = {navController.navigate("${MagazynDestination.route}/$it")},
                onButtonZaleceniaClicked = { navController.navigate("${ZaleceniaDestination.route}/$it") },
                onButtonPowiadomieniaClicked = { navController.navigate("${PowiadomieniaDestination.route}/$it") },
                onButtonUstawieniaClicked = { navController.navigate("${UstawieniaDestination.route}/$it") },
                onButtonPatientClicked = {navController.navigate("${PatientsDestination.route}/$it")}
            )
        }

        composable(route = MagazynDestination.routeWithArgs, arguments = listOf(navArgument(MagazynDestination.patientIdArg){
            type = NavType.IntType
        })) {
            MagazynScreen(
                onButtonHomeClick = {navController.navigate("${HomeDestination.route}/${it}")},
                onButtonMagazynClicked = { },
                onButtonZaleceniaClicked = { navController.navigate("${ZaleceniaDestination.route}/$it") },
                onButtonPowiadomieniaClicked = { navController.navigate("${PowiadomieniaDestination.route}/$it") },
                onButtonUstawieniaClicked = { navController.navigate("${UstawieniaDestination.route}/$it") },
                onButtonPatientClicked = {navController.navigate("${PatientsDestination.route}/$it")},
                navigateToStorage = {}
            )
        }


        composable(route = ZaleceniaDestination.routeWithArgs, arguments = listOf(navArgument(ZaleceniaDestination.patientIdArg){
            type = NavType.IntType
        })) {
            ZaleceniaScreen(
                onButtonHomeClick = {navController.navigate("${HomeDestination.route}/${it}")},
                onButtonMagazynClicked =  {navController.navigate("${MagazynDestination.route}/$it")},
                onButtonZaleceniaClicked = { },
                onButtonPowiadomieniaClicked = { navController.navigate("${PowiadomieniaDestination.route}/$it") },
                onButtonUstawieniaClicked = { navController.navigate("${UstawieniaDestination.route}/$it") },
                onButtonPatientClicked = {navController.navigate("${PatientsDestination.route}/$it")}
            )
        }

        composable(route = PowiadomieniaDestination.routeWithArgs, arguments = listOf(navArgument(PowiadomieniaDestination.patientIdArg){
            type = NavType.IntType
        })) {
            PowiadomieniaScreen(
                onButtonHomeClick = {navController.navigate("${HomeDestination.route}/${it}")},
                onButtonMagazynClicked = {navController.navigate("${MagazynDestination.route}/$it")},
                onButtonZaleceniaClicked = { navController.navigate("${ZaleceniaDestination.route}/$it") },
                onButtonPowiadomieniaClicked = {  },
                onButtonUstawieniaClicked = { navController.navigate("${UstawieniaDestination.route}/$it") },
                onButtonPatientClicked = {navController.navigate("${PatientsDestination.route}/$it")}
            )
        }


        composable(route = UstawieniaDestination.routeWithArgs, arguments = listOf(navArgument(UstawieniaDestination.patientIdArg){
            type = NavType.IntType
        })) {
            UstawieniaScreen(
                onButtonHomeClick = {navController.navigate("${HomeDestination.route}/${it}")},
                onButtonMagazynClicked =  {navController.navigate("${MagazynDestination.route}/$it")},
                onButtonZaleceniaClicked = { navController.navigate("${ZaleceniaDestination.route}/$it") },
                onButtonPowiadomieniaClicked = { navController.navigate("${PowiadomieniaDestination.route}/$it") },
                onButtonUstawieniaClicked = { },
                onButtonPatientClicked = {navController.navigate("${PatientsDestination.route}/$it")}
            )
        }

        composable(route=PatientsDestination.routeWithArgs, arguments = listOf(navArgument(PatientsDestination.patientIdArg){
            type = NavType.IntType
        })) {
            PatientsListScreen(
                onBack={ navController.popBackStack() },
                navigateToPatientHome={navController.navigate("${HomeDestination.route}/${it}")},
            )
        }

        composable(route=PatientsDestination.route) {
            PatientsListScreen(
                onBack={ navController.popBackStack() },
                navigateToPatientHome={navController.navigate("${HomeDestination.route}/${it}")},
            )
        }

    }

}

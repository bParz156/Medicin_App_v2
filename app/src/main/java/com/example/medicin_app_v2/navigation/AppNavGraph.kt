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
import com.example.medicin_app_v2.ui.powiadomienia.PowiadomieniaScreen
import com.example.medicin_app_v2.ui.ustawienia.UstawieniaScreen
import com.example.medicin_app_v2.ui.zalecenia.ZaleceniaDestination
import com.example.medicin_app_v2.ui.zalecenia.ZaleceniaScreen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MedicinNavHost(
    navController: NavHostController,
    modifier: Modifier = Modifier,
) {
    val id=-1

    NavHost(
        navController = navController,
        startDestination = "${HomeDestination.route}/$id",
        modifier = modifier
    ) {
    /*
        composable(route = HomeDestination.route ) {
            Log.i("przekierowanie", "zly home")
            HomeScreen(
                onButtonHomeClick = {},
                onButtonMagazynClicked = { navController.navigate(Location.MAGAZYN.name) },
                onButtonZaleceniaClicked = { navController.navigate(Location.ZALECENIA.name) },
                onButtonPowiadomieniaClicked = { navController.navigate(Location.POWIADOMIENIA.name) },
                onButtonUstawieniaClicked = { navController.navigate(Location.USTAWIENIA.name) },
                onButtonPatientClicked = {navController.navigate(PatientsDestination.route)}
            )
        }
*/


        composable(route = HomeDestination.routeWithArgs, arguments = listOf(navArgument(HomeDestination.patientIdArg){
            type = NavType.IntType
        })) {
            Log.i("przekierowanie", "dobry home")
            HomeScreen(
                onButtonHomeClick = {},
                onButtonMagazynClicked = {navController.navigate("${MagazynDestination.route}/$it")},
                onButtonZaleceniaClicked = { navController.navigate("${ZaleceniaDestination.route}/$it") },
                onButtonPowiadomieniaClicked = { navController.navigate(Location.POWIADOMIENIA.name) },
                onButtonUstawieniaClicked = { navController.navigate(Location.USTAWIENIA.name) },
                onButtonPatientClicked = {navController.navigate("${PatientsDestination.route}/$it")}
            )
        }

        composable(route = MagazynDestination.routeWithArgs, arguments = listOf(navArgument(HomeDestination.patientIdArg){
            type = NavType.IntType
        })) {
            MagazynScreen(
                onButtonHomeClick = {navController.navigate("${HomeDestination.route}/${it}")},
                onButtonMagazynClicked = { },
                onButtonZaleceniaClicked = { navController.navigate("${ZaleceniaDestination.route}/$it") },
                onButtonPowiadomieniaClicked = { navController.navigate(Location.POWIADOMIENIA.name) },
                onButtonUstawieniaClicked = { navController.navigate(Location.USTAWIENIA.name) },
                onButtonPatientClicked = {navController.navigate("${PatientsDestination.route}/$it")},
                navigateToStorage = {}
            )
        }


        composable(route = ZaleceniaDestination.routeWithArgs, arguments = listOf(navArgument(HomeDestination.patientIdArg){
            type = NavType.IntType
        })) {
            ZaleceniaScreen(
                onButtonHomeClick = {navController.navigate("${HomeDestination.route}/${it}")},
                onButtonMagazynClicked =  {navController.navigate("${MagazynDestination.route}/$it")},
                onButtonZaleceniaClicked = { },
                onButtonPowiadomieniaClicked = { navController.navigate(Location.POWIADOMIENIA.name) },
                onButtonUstawieniaClicked = { navController.navigate(Location.USTAWIENIA.name) },
                onButtonPatientClicked = {navController.navigate("${PatientsDestination.route}/$it")}
            )
        }


        composable(route = Location.POWIADOMIENIA.name) {
            PowiadomieniaScreen(
                onButtonHomeClick = {navController.navigate(Location.HOME.name)},
                onButtonMagazynClicked = { navController.navigate(Location.MAGAZYN.name) },
                onButtonZaleceniaClicked = { navController.navigate(Location.ZALECENIA.name) },
                onButtonPowiadomieniaClicked = {  },
                onButtonUstawieniaClicked = { navController.navigate(Location.USTAWIENIA.name) },
                onButtonPatientClicked = {navController.navigate(Location.PACJENCI.name)}
            )
        }

        composable(route = Location.USTAWIENIA.name) {
            UstawieniaScreen(
                onButtonHomeClick = {navController.navigate(Location.HOME.name)},
                onButtonMagazynClicked = { navController.navigate(Location.MAGAZYN.name) },
                onButtonZaleceniaClicked = { navController.navigate(Location.ZALECENIA.name) },
                onButtonPowiadomieniaClicked = {  navController.navigate(Location.POWIADOMIENIA.name) },
                onButtonUstawieniaClicked = { },
                onButtonPatientClicked = {navController.navigate(Location.PACJENCI.name)}
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


        /*
        composable(route = Location.HOME.name) {
            HomeScreen(
                    onButtonHomeClick = {},
                    onButtonMagazynClicked = { navController.navigate(Location.MAGAZYN.name) },
                    onButtonZaleceniaClicked = { navController.navigate(Location.ZALECENIA.name) },
                    onButtonPowiadomieniaClicked = { navController.navigate(Location.POWIADOMIENIA.name) },
                    onButtonUstawieniaClicked = { navController.navigate(Location.USTAWIENIA.name) },
                    onButtonPatientClicked = {navController.navigate(Location.PACJENCI.name)}
                )
        }

        composable(route = Location.MAGAZYN.name) {
            MagazynScreen(
                onButtonHomeClick = {navController.navigate(Location.HOME.name)},
                onButtonMagazynClicked = { },
                onButtonZaleceniaClicked = { navController.navigate(Location.ZALECENIA.name) },
                onButtonPowiadomieniaClicked = { navController.navigate(Location.POWIADOMIENIA.name) },
                onButtonUstawieniaClicked = { navController.navigate(Location.USTAWIENIA.name) },
                onButtonPatientClicked = {navController.navigate(Location.PACJENCI.name)}
            )
        }

        composable(route = Location.ZALECENIA.name) {
            ZaleceniaScreen(
                onButtonHomeClick = {navController.navigate(Location.HOME.name)},
                onButtonMagazynClicked = { navController.navigate(Location.MAGAZYN.name) },
                onButtonZaleceniaClicked = { },
                onButtonPowiadomieniaClicked = { navController.navigate(Location.POWIADOMIENIA.name) },
                onButtonUstawieniaClicked = { navController.navigate(Location.USTAWIENIA.name) },
                onButtonPatientClicked = {navController.navigate(Location.PACJENCI.name)}
            )
        }

        composable(route = Location.POWIADOMIENIA.name) {
            PowiadomieniaScreen(
                onButtonHomeClick = {navController.navigate(Location.HOME.name)},
                onButtonMagazynClicked = { navController.navigate(Location.MAGAZYN.name) },
                onButtonZaleceniaClicked = { navController.navigate(Location.ZALECENIA.name) },
                onButtonPowiadomieniaClicked = {  },
                onButtonUstawieniaClicked = { navController.navigate(Location.USTAWIENIA.name) },
                onButtonPatientClicked = {navController.navigate(Location.PACJENCI.name)}
            )
        }

        composable(route = Location.USTAWIENIA.name) {
            UstawieniaScreen(
                onButtonHomeClick = {navController.navigate(Location.HOME.name)},
                onButtonMagazynClicked = { navController.navigate(Location.MAGAZYN.name) },
                onButtonZaleceniaClicked = { navController.navigate(Location.ZALECENIA.name) },
                onButtonPowiadomieniaClicked = {  navController.navigate(Location.POWIADOMIENIA.name) },
                onButtonUstawieniaClicked = { },
                onButtonPatientClicked = {navController.navigate(Location.PACJENCI.name)}
            )
        }
        composable(route=Location.PACJENCI.name) {
            PatientsListScreen(
                onBack={ navController.popBackStack() },
                onPatientClick={},
            )
        }

         */

    }

}

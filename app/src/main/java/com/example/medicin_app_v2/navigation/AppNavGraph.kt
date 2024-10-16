package com.example.medicin_app_v2.navigation

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.medicin_app_v2.ui.CommunUI
import com.example.medicin_app_v2.ui.home.HomeScreen
import com.example.medicin_app_v2.ui.magazyn.MagazynScreen
import com.example.medicin_app_v2.ui.patients.PatientsListScreen
import com.example.medicin_app_v2.ui.powiadomienia.PowiadomieniaScreen
import com.example.medicin_app_v2.ui.ustawienia.UstawieniaScreen
import com.example.medicin_app_v2.ui.zalecenia.ZaleceniaScreen

//import androidx.navigation.NavHostController

@OptIn(ExperimentalMaterial3Api::class)
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
                onAddPatient = {},
                onBack={ navController.popBackStack() },
                onPatientClick={},
                onDeleteClicked={},
                currentPatientIdx=-1,
                patientsList = listOf(),
            )
        }

    }

}

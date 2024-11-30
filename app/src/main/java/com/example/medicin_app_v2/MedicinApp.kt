package com.example.medicin_app_v2

import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.medicin_app_v2.navigation.MedicinNavHost

@SuppressLint("CoroutineCreationDuringComposition")
@Composable
fun MedicinApp(
    id: Int,
    navController: NavHostController = rememberNavController(),
    //viewModel : PatientViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    //val coroutineScope = rememberCoroutineScope()
    //val patient_id by viewModel.patientUPR.collectAsState()
   // coroutineScope.launch {
   //    patient_id = viewModel.getPatientIdFromPreferences()
  //  }
    Log.i("patientId", "MedicinApp 27: $id")
    MedicinNavHost( patientId = id,
        navController = navController)
    //HomeScreen()
}

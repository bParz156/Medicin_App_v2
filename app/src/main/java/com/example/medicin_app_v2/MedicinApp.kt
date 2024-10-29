package com.example.medicin_app_v2

import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.medicin_app_v2.navigation.MedicinNavHost
import com.example.medicin_app_v2.ui.AppViewModelProvider
import com.example.medicin_app_v2.ui.PatientViewModel
import com.example.medicin_app_v2.ui.home.HomeScreen
import kotlinx.coroutines.launch

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

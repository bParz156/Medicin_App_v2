package com.example.medicin_app_v2

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import com.example.medicin_app_v2.data.ThemeMode
import com.example.medicin_app_v2.data.UserPreferencesRepository
import com.example.medicin_app_v2.ui.theme.Medicin_App_v2Theme

class MainActivity : ComponentActivity() {

    private lateinit var userPreferencesRepository: UserPreferencesRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        userPreferencesRepository = UserPreferencesRepository(dataStore)
        enableEdgeToEdge()
        setContent {
            val themeMode by userPreferencesRepository.themeMode.collectAsState(initial = ThemeMode.DAY_MODE)
            val id by userPreferencesRepository.patient_id.collectAsState(initial = 0)
            Medicin_App_v2Theme(themeMode = themeMode) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    MedicinApp(id = id)
                    //HomeScreen()
                }
            }

        }
    }
}


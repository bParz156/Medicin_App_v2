package com.example.medicin_app_v2.ui.ustawienia

import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.medicin_app_v2.data.ThemeMode
import com.example.medicin_app_v2.data.UserPreferencesRepository
import com.example.medicin_app_v2.data.patient.PatientsRepository
import com.example.medicin_app_v2.ui.patients.PatientDetails
import com.example.medicin_app_v2.ui.patients.PatientsDestination
import com.example.medicin_app_v2.ui.patients.toPatientDetails
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class UstawieniaViewModel(
    savedStateHandle: SavedStateHandle,
    private val patientsRepository: PatientsRepository,
    private val userPreferencesRepository: UserPreferencesRepository
) : ViewModel() {

    private val patientId: Int = try {
        checkNotNull(savedStateHandle[PatientsDestination.patientIdArg])
    } catch (e: IllegalStateException) {
        -1
    }
    var ustawieniaUiState by mutableStateOf(UstawieniaUiState())
        private set


    init {
        viewModelScope.launch {

            ustawieniaUiState.patientDetails = patientsRepository.getPatientStream(patientId)
                .filterNotNull()
                .first()
                .toPatientDetails()

            ustawieniaUiState.themeMode = userPreferencesRepository.themeMode.first()
            ustawieniaUiState.scale = (userPreferencesRepository.fontScale.first()*100f).toInt()
        }
    }

    fun getPatientsName(): String{
        return ustawieniaUiState.patientDetails.name
    }

    /**
     * Zmiana kontrastu
     */
    fun setThemeMode(newThemeMode: ThemeMode) {
        viewModelScope.launch {
            userPreferencesRepository.saveThemeMode(newThemeMode)
        }
        ustawieniaUiState.themeMode = newThemeMode
    }


    /**
     * Zmiana wielkości czcionki
     */
    fun setScale(change : Int, context: Context)
    {
        ustawieniaUiState.scale +=change
        val newScale = ustawieniaUiState.scale/100f
        viewModelScope.launch {
            userPreferencesRepository.saveFontScale(newScale)
            userPreferencesRepository.updateFontScale(context, newScale)
          //  userPreferencesRepository.updateFontScaleAndRecreate(LocalContext.current, newScale, this@MainActivity)
        }
    }
}


data class UstawieniaUiState(
    var patientDetails: PatientDetails = PatientDetails(),
    var themeMode: ThemeMode = ThemeMode.DAY_MODE,
    var scale : Int = 100
)
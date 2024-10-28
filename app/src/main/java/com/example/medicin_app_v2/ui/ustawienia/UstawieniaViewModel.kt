package com.example.medicin_app_v2.ui.ustawienia

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.medicin_app_v2.data.ThemeMode
import com.example.medicin_app_v2.data.UserPreferencesRepository
import com.example.medicin_app_v2.data.firstAidKit.FirstaidkitRepository
import com.example.medicin_app_v2.data.medicine.MedicinRepository
import com.example.medicin_app_v2.data.patient.PatientsRepository
import com.example.medicin_app_v2.data.schedule.ScheduleRepository
import com.example.medicin_app_v2.data.scheduleTerms.ScheduleTermRepository
import com.example.medicin_app_v2.data.storage.StorageRepository
import com.example.medicin_app_v2.ui.PatientDetails
import com.example.medicin_app_v2.ui.PatientUiState
import com.example.medicin_app_v2.ui.patients.PatientsDestination
import com.example.medicin_app_v2.ui.toPatientDetails
import com.example.medicin_app_v2.ui.toPatientUiState
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
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
        }
    }

    fun getPatientsName(): String{
        return ustawieniaUiState.patientDetails.name
    }
    // Update theme mode
    fun setThemeMode(newThemeMode: ThemeMode) {
        viewModelScope.launch {
            userPreferencesRepository.saveThemeMode(newThemeMode)
        }
        ustawieniaUiState.themeMode = newThemeMode
    }

}


data class UstawieniaUiState(
    var patientDetails: PatientDetails = PatientDetails(),
    var themeMode: ThemeMode = ThemeMode.DAY_MODE
)
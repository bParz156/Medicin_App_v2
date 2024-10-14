package com.example.medicin_app_v2.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.medicin_app_v2.data.patient.OfflinePatientsRepository
import com.example.medicin_app_v2.data.patient.Patient
import com.example.medicin_app_v2.data.patient.PatientsRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

class HomeViewModel (patientsRepository: PatientsRepository) : ViewModel() {
    val homeUiState : StateFlow<HomeUiState> = patientsRepository.getAllPatientsStream().map { HomeUiState(it) }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(TIMEOUT_MILLIS),
            initialValue = HomeUiState()
        )

    companion object {
        private const val TIMEOUT_MILLIS = 5_000L
    }

}

data class HomeUiState(val patientsList: List<Patient> = listOf())
package com.example.medicin_app_v2.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.medicin_app_v2.data.patient.Patient

class PatientViewModel(private val savedStateHandle: SavedStateHandle) : ViewModel() {

    // Przechowujemy pacjenta w SavedStateHandle
    val selectedPatient: LiveData<Patient?> = savedStateHandle.getLiveData("selectedPatient")

    fun setPatient(patient: Patient) {
        // Zapisujemy pacjenta w SavedStateHandle
        savedStateHandle["selectedPatient"] = patient
    }
}
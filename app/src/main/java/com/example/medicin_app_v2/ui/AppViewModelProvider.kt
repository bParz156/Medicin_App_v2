package com.example.medicin_app_v2.ui

import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory
import androidx.lifecycle.createSavedStateHandle
import androidx.lifecycle.viewmodel.CreationExtras
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.medicin_app_v2.MedicinApplication
import com.example.medicin_app_v2.ui.home.HomeViewModel

object AppViewModelProvider {

    val Factory = viewModelFactory {

        // Initializer for HomeViewModel
        initializer {
            PatientViewModel(
            this.createSavedStateHandle(),
                medicinApplication().container.patientsRepository
            )
        }

        initializer {
            HomeViewModel(
                this.createSavedStateHandle(),
                medicinApplication().container.patientsRepository
            )
        }
    }
}



fun CreationExtras.medicinApplication(): MedicinApplication =
    (this[AndroidViewModelFactory.APPLICATION_KEY] as MedicinApplication)


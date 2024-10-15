package com.example.medicin_app_v2.ui

import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory
import androidx.lifecycle.createSavedStateHandle
import androidx.lifecycle.viewmodel.CreationExtras
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.medicin_app_v2.MedicinApplication

object AppViewModelProvider {

    val Factory = viewModelFactory {

        // Initializer for HomeViewModel
        initializer {
            PatientViewModel(
            this.createSavedStateHandle()
            )
        }
    }
}



fun CreationExtras.medicinApplication(): MedicinApplication =
    (this[AndroidViewModelFactory.APPLICATION_KEY] as MedicinApplication)


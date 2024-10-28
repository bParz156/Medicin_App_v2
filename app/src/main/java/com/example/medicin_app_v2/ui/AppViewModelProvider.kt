package com.example.medicin_app_v2.ui

import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory
import androidx.lifecycle.createSavedStateHandle
import androidx.lifecycle.viewmodel.CreationExtras
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.medicin_app_v2.MedicinApplication
import com.example.medicin_app_v2.data.UserPreferencesRepository
import com.example.medicin_app_v2.ui.home.HomeViewModel
import com.example.medicin_app_v2.ui.magazyn.MagazynViewModel
import com.example.medicin_app_v2.ui.ustawienia.UstawieniaViewModel
import com.example.medicin_app_v2.ui.zalecenia.ZalecenieViewModel

object AppViewModelProvider {

    val Factory = viewModelFactory {

        // Initializer for HomeViewModel
        initializer {
            PatientViewModel(
                savedStateHandle =  this.createSavedStateHandle(),
                patientsRepository =  medicinApplication().container.patientsRepository,
            )
        }

        initializer {
            HomeViewModel(
                savedStateHandle =  this.createSavedStateHandle(),
                patientsRepository =  medicinApplication().container.patientsRepository,
                medicinRepository = medicinApplication().container.medicinRepository,
                scheduleRepository = medicinApplication().container.scheduleRepository,
                scheduleTermRepository = medicinApplication().container.scheduleTermRepository,
                usageRepository = medicinApplication().container.usageRepository
            )
        }

        initializer {
            ZalecenieViewModel(
                savedStateHandle =  this.createSavedStateHandle(),
                medicinRepository = medicinApplication().container.medicinRepository,
                storageRepository = medicinApplication().container.storageRepository,
                scheduleRepository = medicinApplication().container.scheduleRepository,
                patientsRepository = medicinApplication().container.patientsRepository,
                firstaidkitRepository = medicinApplication().container.firstaidkitRepository,
                scheduleTermRepository = medicinApplication().container.scheduleTermRepository
            )
        }

        initializer {
            MagazynViewModel(
                savedStateHandle =  this.createSavedStateHandle(),
                medicinRepository = medicinApplication().container.medicinRepository,
                storageRepository = medicinApplication().container.storageRepository,
                scheduleRepository = medicinApplication().container.scheduleRepository,
                patientsRepository = medicinApplication().container.patientsRepository,
                firstaidkitRepository = medicinApplication().container.firstaidkitRepository,
                scheduleTermRepository = medicinApplication().container.scheduleTermRepository
            )
        }

        initializer {
            UstawieniaViewModel(
                savedStateHandle =  this.createSavedStateHandle(),
                patientsRepository = medicinApplication().container.patientsRepository,
                userPreferencesRepository = medicinApplication().container.userPreferencesRepository
                )
        }

    }
}



fun CreationExtras.medicinApplication(): MedicinApplication =
    (this[AndroidViewModelFactory.APPLICATION_KEY] as MedicinApplication)


package com.example.medicin_app_v2.ui

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.LiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.medicin_app_v2.data.patient.Patient
import com.example.medicin_app_v2.data.patient.PatientsRepository
import com.example.medicin_app_v2.ui.home.HomeDestination
import com.example.medicin_app_v2.ui.patients.PatientsDestination
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

class PatientViewModel(
    savedStateHandle: SavedStateHandle,
    private val patientsRepository: PatientsRepository) : ViewModel()
{
        private val patientId : Int = try{checkNotNull(savedStateHandle[PatientsDestination.patientIdArg])}
        catch (e:IllegalStateException)
        {
            -1
        }

    var patientUiState by mutableStateOf(PatientUiState())
        private  set

    fun updatePatientUiState(patientDetails: PatientDetails)
    {
        patientUiState = if(patientDetails.name.isNotBlank()) PatientUiState(patientDetails=patientDetails)
        else patientUiState
    }

    val uiState: StateFlow<PatientUiState> = patientsRepository.getPatientStream(patientId)
        .filterNotNull()
        .map {  PatientUiState(patientDetails = it.toPatientDetails())}
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(TIMEOUT_MILLIS),
            initialValue = PatientUiState()
        )

    fun updatetUiState(patientDetails: PatientDetails)
    {
        patientUiState =  PatientUiState(patientDetails=patientDetails)
    }



    val patientslistUiState : StateFlow<PatientListUiState> =
        patientsRepository.getAllPatientsStream().map { PatientListUiState(it) }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(TIMEOUT_MILLIS),
                initialValue = PatientListUiState()
            )

    suspend fun deletePatient()
    {
        patientsRepository.deletePatient(patientUiState.patientDetails.toPatient())
    }

    suspend fun createPatient()
    {
        if(patientUiState.patientDetails.name.isNotBlank())
        {
            patientsRepository.insertPatient(patientUiState.patientDetails.toPatient())
        }

    }


    companion object {
        const val TIMEOUT_MILLIS = 5_000L
    }

}

/**
 * Ui State for PatientList
 */
data class PatientListUiState(val patientList: List<Patient> = listOf())



data class PatientUiState(
    val patientDetails : PatientDetails = PatientDetails(),
)

data class PatientDetails(
    val id: Int = 0,
    var name: String =""
)

fun PatientDetails.toPatient() : Patient =Patient(
    id=id,
    name = name
)

fun Patient.toPatientDetails() :PatientDetails = PatientDetails(
    id=id,
    name=name
)

fun Patient.toPatientUiState(): PatientUiState = PatientUiState(
    patientDetails = this.toPatientDetails(),
)



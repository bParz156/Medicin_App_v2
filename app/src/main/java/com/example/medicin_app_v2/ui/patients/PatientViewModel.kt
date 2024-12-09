package com.example.medicin_app_v2.ui.patients

import android.util.Log
import androidx.compose.animation.core.rememberTransition
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.medicin_app_v2.data.UserPreferencesRepository
import com.example.medicin_app_v2.data.patient.Patient
import com.example.medicin_app_v2.data.patient.PatientsRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class PatientViewModel(
    savedStateHandle: SavedStateHandle,
    private val patientsRepository: PatientsRepository,
    private val userPreferencesRepository: UserPreferencesRepository
) : ViewModel()
{
        var patientUPR: StateFlow<Int> = userPreferencesRepository.patient_id
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(), -1)


        private var patientId : Int = try{checkNotNull(savedStateHandle[PatientsDestination.patientIdArg])}
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
        .map {  PatientUiState(patientDetails = it.toPatientDetails()) }
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

    suspend fun createPatient() : Boolean
    {
        if(patientUiState.patientDetails.name.isNotBlank() && canCreatePatient())
        {
            patientsRepository.insertPatient(patientUiState.patientDetails.toPatient())
            return true
        }
        return false

    }

    /**
     * Pacjenta można stworzyć tylko jeśli nie istnieje pacjent o takim samym imieniu
     */
    suspend fun canCreatePatient() : Boolean
    {
        val patient = patientsRepository.getPatientByName(patientUiState.patientDetails.name).first()
        return patient==null
    }

    /**
     * Zmiana wybranego pacjenta i zapisanie zmiany w preferencjach
     */
    suspend fun changePatientId(id: Int)
    {
        viewModelScope.launch {
            Log.i("patientId", "z chabge przed savem: id =$id")
            userPreferencesRepository.savePatientId(id)
           // Log.i("patientId", "change po save")
            patientUPR = userPreferencesRepository.patient_id
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(), id)
            Log.i("patientId", "change po save patientUPR: ${patientUPR.value}")
        }
        patientId =id

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

fun Patient.toPatientDetails() : PatientDetails = PatientDetails(
    id=id,
    name=name
)

fun Patient.toPatientUiState(): PatientUiState = PatientUiState(
    patientDetails = this.toPatientDetails(),
)



package com.example.medicin_app_v2.ui.home

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.medicin_app_v2.data.patient.Patient
import com.example.medicin_app_v2.data.patient.PatientsRepository
import com.example.medicin_app_v2.ui.PatientUiState
import com.example.medicin_app_v2.ui.patients.PatientsDestination
import com.example.medicin_app_v2.ui.toPatientDetails
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn


class HomeViewModel (savedStateHandle: SavedStateHandle,
                     patientsRepository: PatientsRepository

) : ViewModel()
{
    private val patientId : Int = try{checkNotNull(savedStateHandle[HomeDestination.patientIdArg])}
    catch (e:IllegalStateException)
    {
        -1
    }

    val uiState: StateFlow<PatientUiState> = patientsRepository.getPatientStream(patientId)
        .filterNotNull()
        .map {  PatientUiState(patientDetails = it.toPatientDetails()) }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(TIMEOUT_MILLIS),
            initialValue = PatientUiState()
        )

    companion object {
        private const val TIMEOUT_MILLIS = 5_000L
    }


}



/*
class HomeViewModel(private val patient: Flow<Patient?>) : ViewModel()
{
    private val _homeUiState = MutableStateFlow(HomeUiState(patient))
    val homeUiState: StateFlow<HomeUiState> = _homeUiState.asStateFlow()

    fun getPatientsName() : String
    {
        return patient.toString()
    }
}

/**
 * Ui State for HomeScreen
 */
data class HomeUiState(val patient: Flow<Patient?>? = null)

/*
To jest bardziej odpowiednie do PatientsScreenAdd

class HomeViewModel (private val patientsRepository: PatientsRepository) : ViewModel() {

    var patientUiState by mutableStateOf(PatientUiState())
        private set


    fun updateUiState(patientDetails: PatientDetails) {
        patientUiState =
            PatientUiState(patientDetails = patientDetails, isEntryValid = validateInput(patientDetails))
    }

    private fun validateInput(uiState: PatientDetails = patientUiState.patientDetails): Boolean {
        return with(uiState) {
            name.isNotBlank()
        }
    }

    suspend fun savePatient() {
        if (validateInput()) {
            patientsRepository.insertPatient(patientUiState.patientDetails.toPatient())
        }
    }

}

data class PatientUiState(
    val patientDetails : PatientDetails = PatientDetails(),
    val isEntryValid : Boolean = false
)

data class PatientDetails(
    val id: Int = 0,
    val name: String =""
)

fun PatientDetails.toPatient() : Patient =Patient(
    id=id,
    name = name
)

fun Patient.toPatientDetails() :PatientDetails = PatientDetails(
    id=id,
    name=name
)

fun Patient.toPatientUiState(isEntryValid: Boolean = false): PatientUiState = PatientUiState(
    patientDetails = this.toPatientDetails(),
    isEntryValid = isEntryValid
)


/*
To jest bardziej odpowiednie do PatientsScreen

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

 */
package com.example.medicin_app_v2.ui.home

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.medicin_app_v2.data.DayWeek
import com.example.medicin_app_v2.data.MealRelation
import com.example.medicin_app_v2.data.MedicinForm
import com.example.medicin_app_v2.data.medicine.MedicinRepository
import com.example.medicin_app_v2.data.medicine.Medicine
import com.example.medicin_app_v2.data.patient.Patient
import com.example.medicin_app_v2.data.patient.PatientsRepository
import com.example.medicin_app_v2.data.schedule.Schedule
import com.example.medicin_app_v2.data.schedule.ScheduleRepository
import com.example.medicin_app_v2.data.storage.StorageRepository
import com.example.medicin_app_v2.ui.PatientDetails
import com.example.medicin_app_v2.ui.PatientListUiState
import com.example.medicin_app_v2.ui.PatientUiState
import com.example.medicin_app_v2.ui.PatientViewModel
import com.example.medicin_app_v2.ui.PatientViewModel.Companion
import com.example.medicin_app_v2.ui.patients.PatientsDestination
import com.example.medicin_app_v2.ui.toPatientDetails
import com.example.medicin_app_v2.ui.toPatientUiState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.Date


class HomeViewModel (savedStateHandle: SavedStateHandle,
                     patientsRepository: PatientsRepository,
    scheduleRepository: ScheduleRepository,
    medicinRepository: MedicinRepository

) : ViewModel()
{

    var homeUiState by mutableStateOf(PatientUiState())
        private set

    var patientsSchedule by mutableStateOf(PatientScheduleDetailsInfo())
        private set

    private val patientId : Int = try{checkNotNull(savedStateHandle[HomeDestination.patientIdArg])}
    catch (e:IllegalStateException)
    {
        -1
    }


    init {
        viewModelScope.launch {
            homeUiState = patientsRepository.getPatientStream(patientId)
                .filterNotNull()
                .first()
                .toPatientUiState()


            val Schedules = scheduleRepository.getAllPatientsSchedules(patientId)
                .first() // Poczekaj na pierwszy wynik z Flow
                .let { PatientScheduleInfo(it) } // Zmapuj wynik na PatientScheduleInfo

            patientsSchedule =PatientScheduleDetailsInfo(
                Schedules.scheduleList.map { it.toScheduleDetails(
                    medicinDetails = medicinRepository.getMedicineStream(it.Medicine_id)
                        .filterNotNull()
                        .first()
                        .toMedicinDetails()
                )
                })


        }
    }




    fun getPatientsName(): String{
        return homeUiState.patientDetails.name
    }



}

data class PatientScheduleDetailsInfo(
    val scheduleDetailsList: List<ScheduleDetails> = listOf()
)

data class PatientScheduleInfo(
    val scheduleList: List<Schedule> = listOf()
)

data class ScheduleDetails(
    val id : Int = 0,
    var medicinDetails: MedicinDetails = MedicinDetails(),
    var day: DayWeek = DayWeek.PON,
    var hour: Int = 0,
    var minute: Int = 0,
    var dose: Int =0,
    var startDate: Date = Date(),
    val endDate: Date? = null,
    var mealRelation: MealRelation = MealRelation.Nie
) {
    fun isValid(): Boolean {
        return medicinDetails.name.isNotBlank()
    }
}


fun ScheduleDetails.toSchedule(patiendId : Int): Schedule = Schedule(
    id=id,
    Patient_id = patiendId,
    Medicine_id = medicinDetails.id,
    day = day,
    hour= hour,
    minute = minute,
    dose= dose,
    startDate = startDate,
    endDate = endDate,
    mealRelation = mealRelation
)

fun Schedule.toScheduleDetails(medicinDetails: MedicinDetails) : ScheduleDetails = ScheduleDetails(
    id=id,
    medicinDetails = medicinDetails,
    day = day,
    hour= hour,
    minute = minute,
    dose= dose,
    startDate = startDate,
    endDate = endDate,
    mealRelation = mealRelation
)


data class MedicinDetails(
    val id: Int=0,
    var name: String ="",
    val form: MedicinForm = MedicinForm.TABLETKA
)

fun MedicinDetails.toMedicin() : Medicine = Medicine(
    id=id,
    name=name,
    form = form
)

fun Medicine.toMedicinDetails() : MedicinDetails = MedicinDetails(
    id=id,
    name=name,
    form = form
)



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
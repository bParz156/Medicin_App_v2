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
import com.example.medicin_app_v2.data.scheduleTerms.ScheduleTerm
import com.example.medicin_app_v2.data.medicine.MedicinRepository
import com.example.medicin_app_v2.data.medicine.Medicine
import com.example.medicin_app_v2.data.patient.PatientsRepository
import com.example.medicin_app_v2.data.schedule.Schedule
import com.example.medicin_app_v2.data.schedule.ScheduleRepository
import com.example.medicin_app_v2.data.scheduleTerms.ScheduleTermRepository
import com.example.medicin_app_v2.ui.PatientUiState
import com.example.medicin_app_v2.ui.toPatientUiState
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.util.Date


class HomeViewModel (
    savedStateHandle: SavedStateHandle,
 patientsRepository: PatientsRepository,
    scheduleRepository: ScheduleRepository,
 scheduleTermRepository: ScheduleTermRepository,
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
                        .toMedicinDetails(),
                    scheduleTermList =  scheduleTermRepository.getAllsSchedulesTerms(it.id).filterNotNull().first()

                )
                })


        }
    }




    fun getPatientsName(): String{
        return homeUiState.patientDetails.name
    }



}

data class PatientScheduleDetailsInfo(
    var scheduleDetailsList: List<ScheduleDetails> = listOf()
)

data class PatientScheduleInfo(
    val scheduleList: List<Schedule> = listOf()
)

data class ScheduleDetails(
    val id : Int = 0,
    var medicinDetails: MedicinDetails = MedicinDetails(),
    var startDate: Date = Date(),
    var endDate: Date? = null,
    var scheduleTermDetailsList: List<ScheduleTermDetails> = listOf()
)

data class ScheduleTermDetails(
    var day: DayWeek = DayWeek.PON,
    var hour: Int =0,
    var minute: Int =0,
    var dose: Int =0
) {
    fun toScheduleTerm(scheduleId: Int) : ScheduleTerm = ScheduleTerm(
        dose = dose,
        day =day,
        minute = minute,
        hour = hour,
        ScheduleId = scheduleId
    )
}


fun ScheduleDetails.toSchedule(patiendId : Int): Schedule = Schedule(
    id=id,
    Patient_id = patiendId,
    Medicine_id = medicinDetails.id,
    startDate = startDate,
    endDate = endDate,
)

fun Schedule.toScheduleDetails(medicinDetails: MedicinDetails, scheduleTermList: List<ScheduleTerm>) : ScheduleDetails = ScheduleDetails(
    id=id,
    medicinDetails = medicinDetails,
    startDate = startDate,
    endDate = endDate,
    scheduleTermDetailsList = scheduleTermList.map {
        it.toScheduleTermDetails()
    }
)


data class MedicinDetails(
    var id: Int=0,
    var name: String ="",
    val form: MedicinForm = MedicinForm.TABLETKA,
    val relation: MealRelation = MealRelation.Nie
)

fun MedicinDetails.toMedicin() : Medicine = Medicine(
    id=id,
    name=name,
    form = form,
    mealRelation = relation
)

fun Medicine.toMedicinDetails() : MedicinDetails = MedicinDetails(
    id=id,
    name=name,
    form = form,
    relation = mealRelation
)


fun ScheduleTerm.toScheduleTermDetails(): ScheduleTermDetails = ScheduleTermDetails(
    minute = minute,
    hour = hour,
    day = day,
    dose = dose
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
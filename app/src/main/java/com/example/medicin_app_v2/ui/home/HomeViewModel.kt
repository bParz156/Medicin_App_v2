package com.example.medicin_app_v2.ui.home

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.medicin_app_v2.data.DayWeek
import com.example.medicin_app_v2.data.MealRelation
import com.example.medicin_app_v2.data.MedicinForm
import com.example.medicin_app_v2.data.UserPreferencesRepository
import com.example.medicin_app_v2.data.WorkerRepository
import com.example.medicin_app_v2.data.medicine.MedicinRepository
import com.example.medicin_app_v2.data.medicine.Medicine
import com.example.medicin_app_v2.data.patient.PatientsRepository
import com.example.medicin_app_v2.data.schedule.Schedule
import com.example.medicin_app_v2.data.schedule.ScheduleRepository
import com.example.medicin_app_v2.data.scheduleTerms.ScheduleTerm
import com.example.medicin_app_v2.data.scheduleTerms.ScheduleTermRepository
import com.example.medicin_app_v2.data.usage.Usage
import com.example.medicin_app_v2.data.usage.UsageRepository
import com.example.medicin_app_v2.ui.PatientUiState
import com.example.medicin_app_v2.ui.toPatientUiState
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.util.Calendar
import java.util.Date


class HomeViewModel (
    savedStateHandle: SavedStateHandle,
    patientsRepository: PatientsRepository,
    scheduleRepository: ScheduleRepository,
    scheduleTermRepository: ScheduleTermRepository,
    medicinRepository: MedicinRepository,
    val usageRepository: UsageRepository,
    userPreferencesRepository: UserPreferencesRepository,
    workerRepository: WorkerRepository

) : ViewModel()
{

    var homeUiState by mutableStateOf(HomeUiState())
        private set

    private var patientsSchedule by mutableStateOf(PatientScheduleDetailsInfo())
        private set


//    val patientId: Int = viewModelScope.launch {
//        userPreferencesRepository.patient_id.first()
//            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(), -1)
//    }


    private var patientId : Int = try{checkNotNull(savedStateHandle[HomeDestination.patientIdArg])}
    catch (e:IllegalStateException)
    {
        viewModelScope.launch {
            userPreferencesRepository.patient_id.first()
        }
        Log.i("patientId", "czytanie z Home 61: ${userPreferencesRepository.patient_id}")
        //patientIdFlow.value
        //-1
    }


//    private var patientId : Int = try{checkNotNull(savedStateHandle[HomeDestination.patientIdArg])}
//    catch (e:IllegalStateException)
//    {
//        viewModelScope.launch {
//           userPreferencesRepository.patient_id.first()
//       }
//    }

    init {

        viewModelScope.launch {
        //    val patient_Id: Int = userPreferencesRepository.patient_id.first()
        //    Log.i("homeeee", "start init: $patientId -- ${patient_Id}")
       //     patientId = patient_Id

            if(patientId==-1)
            {
                viewModelScope.launch {
                   patientId = userPreferencesRepository.patient_id.first()
                    Log.i("patientId", "BYło -1 więc teraz zmiana")
                }
            }
            Log.i("patientId", "init, id z savedStateHandle: ${patientId}")
            homeUiState.patientUiState = patientsRepository.getPatientStream(patientId)
                .filterNotNull()
                .first()
                .toPatientUiState()
            Log.i("homeeee", "65")
        if(patientId!=-1) {
            Log.i("homeeee", "id istnieje")
            val Schedules = scheduleRepository.getAllPatientsSchedules(patientId)
                .first() // Poczekaj na pierwszy wynik z Flow
                .let { PatientScheduleInfo(it) } // Zmapuj wynik na PatientScheduleInfo

            patientsSchedule = PatientScheduleDetailsInfo(
                Schedules.scheduleList.map {
                    it.toScheduleDetails(
                        medicinDetails = medicinRepository.getMedicineStream(it.Medicine_id)
                            .filterNotNull()
                            .first()
                            .toMedicinDetails(),
                        scheduleTermList = scheduleTermRepository.getAllsSchedulesTerms(it.id)
                            .filterNotNull().first()

                    )
                })

            for(scheduleDetail in patientsSchedule.scheduleDetailsList)
            {
                for(scheduleTerm in scheduleDetail.scheduleTermDetailsList)
                {
                    val usageList = usageRepository.getAllScheduleTermUsages(scheduleTerm.id).filterNotNull().first()

                    for(usage in usageList)
                    {
                        homeUiState.usageMapDay.getOrPut(usage.date) { mutableListOf() }.add(UsageDetails(
                            id = usage.id,
                            date = usage.date,
                            dose = scheduleTerm.dose,
                            medicinDetails = scheduleDetail.medicinDetails,
                            confirmed = usage.confirmed
                        ))

                    }

                }
            }
            workerRepository.generateUsages()

        //    genereteUsagesForNextPeriodOfTime(7)
        }
        }
    }




    fun getPatientsName(): String{
        Log.i("homeeee", "get name")
        return homeUiState.patientUiState.patientDetails.name
    }

    suspend fun genereteUsagesForNextPeriodOfTime(days: Int)
    {
        val calendar = Calendar.getInstance() // Bieżąca data
        val currentDate = calendar.time
        for(scheduleDetail in patientsSchedule.scheduleDetailsList)
        {
            for(scheduleTerm in scheduleDetail.scheduleTermDetailsList)
            {
                for(dayOffset in 0..days)
                {
                    calendar.time = currentDate
                    calendar.add(Calendar.DAY_OF_YEAR, dayOffset)
                    val eventDate = calendar.time
                    if(isValidEventDay(scheduleTerm, calendar))
                    {
                        calendar.set(Calendar.HOUR_OF_DAY, scheduleTerm.hour)
                        calendar.set(Calendar.MINUTE, scheduleTerm.minute)
                        val eventTime = calendar.time
                        Log.i("usageeee", "id scheduleTerm = ${scheduleTerm.id}")
                        val usage = Usage(id=0,
                            ScheduleTerm_id = scheduleTerm.id,
                            confirmed = false,
                            date = eventTime)
                        Log.i("usageeee", " Create usage scheudle Term = ${usage.ScheduleTerm_id}")
                        val id = usageRepository.insert(usage)
                        Log.i("usageeee", " id created = $id")
//                        // homeUiState.usageList.add(usage)
////                        homeUiState.usageList.add(
////                            UsageDetails(
////                                id = id.toInt(),
////                                date = eventTime,
////                                dose = scheduleTerm.dose,
////                                medicinDetails = scheduleDetail.medicinDetails
////                            )
//                        )
                       // Log.i("usageeee", " w usageList= id= $id , date.hou")
                    }

                }
            }
        }



    }


    private fun isValidEventDay(scheduleTerm: ScheduleTermDetails, calendar: Calendar): Boolean {
        val dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK)  // Zwraca dzień tygodnia: 1 = Niedziela, 7 = Sobota
        Log.i("usageeee", "isValidDay  == scheudleDay: $(${scheduleTerm.day.weekDay}) ==? ${dayOfWeek}")
        return scheduleTerm.day.weekDay == dayOfWeek  // Załóżmy, że 'dayOfWeek' w harmonogramie jest zgodne z `Calendar`
    }



}

data class HomeUiState(
    var patientUiState: PatientUiState = PatientUiState(),
    val usageMapDay: MutableMap<Date, MutableList<UsageDetails>> = mutableMapOf()
 //   val usageList : List<UsageDetails> = listOf()
)


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
    var id: Int =0,
    var day: DayWeek = DayWeek.PON,
    var hour: Int =0,
    var minute: Int =0,
    var dose: Int =0
) {
    fun toScheduleTerm(scheduleId: Int) : ScheduleTerm = ScheduleTerm(
        id=id,
        dose = dose,
        day =day,
        minute = minute,
        hour = hour,
        ScheduleId = scheduleId
    )
}



data class UsageDetails(
    val id : Int = 0,
    val confirmed : Boolean = false,
    val date: Date,
    val dose: Int = 0,
    val medicinDetails: MedicinDetails = MedicinDetails()

)


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
    id =id,
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
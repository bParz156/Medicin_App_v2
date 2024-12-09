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
import com.example.medicin_app_v2.data.firstAidKit.FirstaidkitRepository
import com.example.medicin_app_v2.data.medicine.MedicinRepository
import com.example.medicin_app_v2.data.medicine.Medicine
import com.example.medicin_app_v2.data.patient.PatientsRepository
import com.example.medicin_app_v2.data.schedule.Schedule
import com.example.medicin_app_v2.data.schedule.ScheduleRepository
import com.example.medicin_app_v2.data.scheduleTerms.ScheduleTerm
import com.example.medicin_app_v2.data.scheduleTerms.ScheduleTermRepository
import com.example.medicin_app_v2.data.storage.Storage
import com.example.medicin_app_v2.data.storage.StorageRepository
import com.example.medicin_app_v2.data.usage.Usage
import com.example.medicin_app_v2.data.usage.UsageRepository
import com.example.medicin_app_v2.ui.patients.PatientUiState
import com.example.medicin_app_v2.ui.patients.toPatientUiState
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable
import java.util.Calendar
import java.util.Date


class HomeViewModel (
    savedStateHandle: SavedStateHandle,
    patientsRepository: PatientsRepository,
    scheduleRepository: ScheduleRepository,
    scheduleTermRepository: ScheduleTermRepository,
    medicinRepository: MedicinRepository,
    val usageRepository: UsageRepository,
    val storageRepository: StorageRepository,
    val firstaidkitRepository: FirstaidkitRepository,
    userPreferencesRepository: UserPreferencesRepository,
    workerRepository: WorkerRepository

) : ViewModel()
{

    var homeUiState by mutableStateOf(HomeUiState())
        private set

    // harmonogram zażywania leków pacjenta
    private var patientsSchedule by mutableStateOf(PatientScheduleDetailsInfo())
        private set

    // aptecznik pacjenta - przyporządkowanie w mapie id leku do magazynu pacjenta
    private val medicinStorage : MutableMap<Int, Storage> = mutableMapOf()




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


// pobranie pacjenta z bazy, jego harmonogramu zażywania leku oraz magazynów
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
            val calendar = Calendar.getInstance()

            //aptecnzki pacjeta
            val firstAidKits = firstaidkitRepository.getAllFirstAidKitsStream(patientId)
                .first()
            //val storages : MutableList<Storage> = mutableListOf()
            for(kit in firstAidKits)
            {
                //na podsrtawie apteczki znalezeinei magazynu
                val storage = storageRepository.getStorageStream(kit.Storage_id).filterNotNull().first()
                medicinStorage[storage.Medicineid] = storage
            }

            for(scheduleDetail in patientsSchedule.scheduleDetailsList)
            {

                for(scheduleTerm in scheduleDetail.scheduleTermDetailsList)
                {
                    val usageList = usageRepository.getAllScheduleTermUsages(scheduleTerm.id).filterNotNull().first()

                    for(usage in usageList)
                    {
                        calendar.time = usage.date
                        val day = calendar.get(Calendar.DAY_OF_MONTH)
                        val month = calendar.get(Calendar.MONTH)
                        val year = calendar.get(Calendar.YEAR)
                        val eventDate = Date(year - 1900, month, day)
                        //umiesc w mapie zażywania leków w dni zażycie - jeśli ten dzień już istniał dodaj zażycie do listy, w przeciwnym wypadku uwtorz nową parę klucz-wartość w mapie
                        homeUiState.usageMapDay.getOrPut(eventDate) { mutableListOf() }.add(UsageDetails(
                            id = usage.id,
                            date = usage.date,
                            dose = scheduleTerm.dose,
                            medicinDetails = scheduleDetail.medicinDetails,
                            confirmed = usage.confirmed,
                            scheduleTermId = scheduleTerm.id,
                            storageId = medicinStorage[scheduleDetail.medicinDetails.id]?.id ?: 0,
                            patientsName = getPatientsName()
                        ))

                    }

                }
            }
            //posortuj mapę według dnii
            homeUiState.usageMapDay = homeUiState.usageMapDay.toSortedMap()
                .mapValues { entry ->
                    entry.value.sortedBy { it.date }
                } as MutableMap<Date, MutableList<UsageDetails>>

            ///uruchom zadania - usun archiwalne zażycia, powiadomienia, wygeneruj nowe powiadomienia, usuń magazyny
            workerRepository.deleteAncient()
            workerRepository.deleteNotifications()
            workerRepository.generateUsages()
            workerRepository.deleteStorages()
            if(homeUiState.usageMapDay.isNotEmpty()) {
                workerRepository.notificationStorage()
                workerRepository.createNotificationsFromUsages(homeUiState.usageMapDay.values.first())
            }

        }
        }
    }




    fun getPatientsName(): String{
        Log.i("homeeee", "get name")
        return homeUiState.patientUiState.patientDetails.name
    }

    /**
     * Potwierdzenie zażycia leku
     */
    fun realizeUsage(usageDetails: UsageDetails)
    {
        usageDetails.confirmed = true
       // val usage = usageDetails.toUsage()
        val storage = medicinStorage[usageDetails.medicinDetails.id]
        if(storage!=null) {
            storage.quantity -= usageDetails.dose
            viewModelScope.launch {
                updateUsageState(usageDetails.toUsage())
                updateStorage(storage)
            }
        }

    }

    suspend fun updateUsageState(usage: Usage)
    {
        usageRepository.update(usage)
    }

    suspend fun updateStorage(storage: Storage)
    {
        storageRepository.updateStorage(storage)
    }


}

data class HomeUiState(
    var patientUiState: PatientUiState = PatientUiState(),
    var usageMapDay: MutableMap<Date, MutableList<UsageDetails>> = mutableMapOf()
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
    var scheduleTermDetailsList: List<ScheduleTermDetails> = listOf(),
    var patiendId: Int = 0
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


@Serializable
data class UsageDetails(
    val id : Int = 0,
    var confirmed : Boolean = false,
    //@Serializable(with = DateAsLongSerializer::class)
    val date: Date,
    val dose: Int = 0,
    val medicinDetails: MedicinDetails = MedicinDetails(),
    val scheduleTermId : Int =0,
    val storageId : Int =0,
    val patientsName : String =""
)


fun UsageDetails.toUsage() : Usage = Usage(
    id= id,
    confirmed = confirmed,
    date = date,
    ScheduleTerm_id = scheduleTermId
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
    },
    patiendId = Patient_id
)

@Serializable
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

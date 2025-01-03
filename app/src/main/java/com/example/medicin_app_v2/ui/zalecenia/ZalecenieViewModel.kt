package com.example.medicin_app_v2.ui.zalecenia

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.medicin_app_v2.data.WorkerRepository
import com.example.medicin_app_v2.data.firstAidKit.FirstAidKit
import com.example.medicin_app_v2.data.firstAidKit.FirstaidkitRepository
import com.example.medicin_app_v2.data.medicine.MedicinRepository
import com.example.medicin_app_v2.data.patient.PatientsRepository
import com.example.medicin_app_v2.data.schedule.ScheduleRepository
import com.example.medicin_app_v2.data.scheduleTerms.ScheduleTermRepository
import com.example.medicin_app_v2.data.storage.Storage
import com.example.medicin_app_v2.data.storage.StorageRepository
import com.example.medicin_app_v2.ui.patients.PatientUiState
import com.example.medicin_app_v2.ui.home.MedicinDetails
import com.example.medicin_app_v2.ui.home.PatientScheduleDetailsInfo
import com.example.medicin_app_v2.ui.home.PatientScheduleInfo
import com.example.medicin_app_v2.ui.home.ScheduleDetails
import com.example.medicin_app_v2.ui.home.toMedicin
import com.example.medicin_app_v2.ui.home.toMedicinDetails
import com.example.medicin_app_v2.ui.home.toSchedule
import com.example.medicin_app_v2.ui.home.toScheduleDetails
import com.example.medicin_app_v2.ui.home.toScheduleTermDetails
import com.example.medicin_app_v2.ui.patients.PatientsDestination
import com.example.medicin_app_v2.ui.patients.toPatientUiState
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class ZalecenieViewModel(
    savedStateHandle: SavedStateHandle,
    private val medicinRepository: MedicinRepository,
    private val storageRepository: StorageRepository,
    private val patientsRepository: PatientsRepository,
    private val scheduleRepository: ScheduleRepository,
    private val firstaidkitRepository: FirstaidkitRepository,
    private val scheduleTermRepository: ScheduleTermRepository,
    private val workerRepository: WorkerRepository
) : ViewModel()
{

    private val patientId : Int = try{checkNotNull(savedStateHandle[PatientsDestination.patientIdArg])}
    catch (e:IllegalStateException)
    {
        -1
    }
    var patientUiState by mutableStateOf(PatientUiState())
        private set

    private var Schedules by mutableStateOf(PatientScheduleInfo())

    var patientsSchedule by mutableStateOf(PatientScheduleDetailsInfo())
        private set

    var scheduleUiState by mutableStateOf(ScheduleUiState())
        private  set

    var storageUiState by mutableStateOf(StorageUiState())
        private set

    private var allMedicinDetails by mutableStateOf(listOf<MedicinDetails>())

    init {
        viewModelScope.launch {

            patientUiState = patientsRepository.getPatientStream(patientId)
                .filterNotNull()
                .first()
                .toPatientUiState()

            Schedules = scheduleRepository.getAllPatientsSchedules(patientId)
                .first() // Poczekaj na pierwszy wynik z Flow
                .let { PatientScheduleInfo(it) } // Zmapuj wynik na PatientScheduleInfo

            patientsSchedule = PatientScheduleDetailsInfo(
                Schedules.scheduleList.map { it.toScheduleDetails(
                    medicinDetails = medicinRepository.getMedicineStream(it.Medicine_id)
                        .filterNotNull()
                        .first()
                        .toMedicinDetails(),
                    scheduleTermList =  scheduleTermRepository.getAllsSchedulesTerms(it.id).filterNotNull().first()
                )
                })
            allMedicinDetails = medicinRepository.getAllMedicinesStream()
                .filterNotNull()
                .first()
                .map { it.toMedicinDetails() }

        }
    }

    fun updateSchedulesInfo()
    {
        viewModelScope.launch {
            Schedules = scheduleRepository.getAllPatientsSchedules(patientId)
                .first() // Poczekaj na pierwszy wynik z Flow
                .let { PatientScheduleInfo(it) } // Zmapuj wynik na PatientScheduleInfo

            patientsSchedule = PatientScheduleDetailsInfo(
                Schedules.scheduleList.map {
                    it.toScheduleDetails(
                        medicinDetails = medicinRepository.getMedicineStream(it.Medicine_id)
                            .filterNotNull()
                            .first()
                            .toMedicinDetails(),
                        scheduleTermList =  scheduleTermRepository.getAllsSchedulesTerms(it.id).filterNotNull().first()
                    )
                })
        }
        workerRepository.generateUsages()

    }

    fun updatestorageState(storageDetails: StorageDetails)
    {
        storageUiState = StorageUiState(storageDetails)
    }


    fun updatetUiState(scheduleDetails: ScheduleDetails)
    {
       // Log.i("createSchedule", "update przed, listSize: ${scheduleUiState.scheduleDetailsList.size}")

        scheduleUiState = ScheduleUiState(scheduleDetails)
      //  Log.i("createSchedule", "update po, listSize: ${scheduleUiState.scheduleDetailsList.size}")

    }

//    suspend fun deleteSchedule()
//    {
//        scheduleRepository.deleteSchedule(scheduleUiState.scheduleDetails.toSchedule(patientId))
//    }


    /**
     * Tworzenie leku, gdy nie istniał
     */
    suspend fun createMedicine()
    {
        Log.i("createSchedule", "createMedicine")
        val medicinDetails = scheduleUiState.scheduleDetails.medicinDetails
        if(medicinDetails.id == 0)
        {
            Log.i("createSchedule", "createMedicine --leku nie bylo")
            val id = medicinRepository.insertMedicine(medicinDetails.toMedicin())
            medicinDetails.id = id.toInt()
            updatetUiState(scheduleDetails = scheduleUiState.scheduleDetails.copy(medicinDetails = medicinDetails))
        }
    }

    /**
     * Tworzenie magazynu, jeśli lek nie istniał, po wpisaniu id leku tworzony jest nowy magayzn i powiązana z nim nowa apteczka,
     * jeśli lek istniał, a magazyn nie, to tworzony jest nowy magayzn i powiązana z nim nowa apteczka (sytuacaj gdy pacjent nie chce współdzielić magazynu)
     * w przeciwnym wypadku tworzona jest apteczka połączaona z istniejącym magazynem.
     */
    suspend fun createStorage()
    {
        Log.i("createSchedule", "createStorage")
        if(storageUiState.storageDetails.MedicinId==0)
        {
            Log.i("createSchedule", "createStorage -- nie bylo id med")
            updatestorageState(storageDetails = storageUiState.storageDetails.copy(MedicinId = scheduleUiState.scheduleDetails.medicinDetails.id))
            val id = storageRepository.insertStorage(storageUiState.storageDetails.toStorage())
            Log.i("createSchedule", "createStorage -- nie bylo id med -- id storage $id")
            firstaidkitRepository.insertFirstAidKit(FirstAidKit(Patient_id= patientId, Storage_id =id.toInt() ))
        }
        else if(storageUiState.storageDetails.id == 0)
        {
            Log.i("createSchedule", "nie bylo id storage")
            val id = storageRepository.insertStorage(storageUiState.storageDetails.toStorage())
            firstaidkitRepository.insertFirstAidKit(FirstAidKit(Patient_id= patientId, Storage_id =id.toInt() ))
        }
        else
        {
            Log.i("createSchedule", "byl storage")
            val id = storageUiState.storageDetails.id
            firstaidkitRepository.insertFirstAidKit(FirstAidKit(Patient_id= patientId, Storage_id =id ))
        }
    }

    /**
     * Tworzenie harmonogramu  oraz powiązane z tym tworzenie Harmonogramu dni
     */
    suspend fun createSchedule()
    {
      //  Log.i("createSchedule", "start, listSize: ${scheduleUiState.scheduleDetailsList.size}")
     //  var medicinDetails = scheduleUiState.scheduleDetailsList.first().medicinDetails
        createMedicine()
        createStorage()
        Log.i("createSchedule", "craeteSchedule")
        val scheduleId = scheduleRepository.insertSchedule( scheduleUiState.scheduleDetails.toSchedule(patientId)).toInt()
        Log.i("createSchedule", "id scheudle: ${scheduleId}")
        for(scheduleTermDetail in scheduleUiState.scheduleDetails.scheduleTermDetailsList)
        {
            scheduleTermDetail.id = 0
            scheduleTermRepository.insertScheduleTerm(scheduleTermDetail.toScheduleTerm(scheduleId))
        }

        updateSchedulesInfo()
        //patientsSchedule.scheduleDetailsList + scheduleUiState.scheduleDetailsList
        Log.i("createSchedule", "lista patienstSchedule zmieniona:")
        scheduleUiState = ScheduleUiState()
        Log.i("createSchedule", "czyszczenie")

    }


    fun getPatientsName(): String{
        return patientUiState.patientDetails.name
    }

    suspend fun deleteZalecenie(){
        scheduleRepository.deleteSchedule(scheduleUiState.scheduleDetails.toSchedule(patientId))
    }
    var medicinSuggestions by mutableStateOf(listOf<MedicinDetails>())

    /**
     * Podpowiadanie użytkownikowi istniejących leków na podstawie wpisanego tekstu
     */
    fun onValueMedicinNameChange(text: String)
    {
        medicinSuggestions = if(text.isEmpty())
        {
            emptyList()
        }
        else
        {
            allMedicinDetails.filter { it.name.startsWith(text, ignoreCase = true) }
        }
    }
    fun getSuggestions() : List<MedicinDetails>
    {
        return medicinSuggestions
    }

    fun getStorage(medicinDetails: MedicinDetails) : MutableMap< StorageDetails, MutableList<String>>
    {
        var map : MutableMap< StorageDetails, MutableList<String>> = mutableMapOf()
        viewModelScope.launch {
            map = getMedicinesStorage(medicinDetails)
        }
        return map
    }

    /**
     * Zebranie listy istniejących magazynów leków oraz informajci i tym, którzy pacjenci z nich korzystają
     */
    suspend private fun getMedicinesStorage(medicinDetails: MedicinDetails) : MutableMap< StorageDetails, MutableList<String>>
    {
        val storageList: List<StorageDetails> = storageRepository.getAllMedicinesStorages(medicine_id = medicinDetails.id)
            .filterNotNull()
            .first()
            .map { it.toStorageDetails() }

        var map : MutableMap< StorageDetails, MutableList<String>> = mutableMapOf()

        for(storageDetials in storageList)
        {
            Log.i("medicine", "viewModel: petla for id storage: ${storageDetials.id}")
            val peopleUsingStorage = firstaidkitRepository.getfirstAidKitByStorage(storageDetials.id).filterNotNull().first().map {
                patientsRepository.getPatientStream(it.Patient_id).filterNotNull().first().name
            }

            val mutableListOfPeople : MutableList<String> = mutableListOf()
            for (people in peopleUsingStorage)
            {
                mutableListOfPeople.add(people)
                Log.i("medicine", "viewModel: petla for w petli for, peopele =: ${people}")

            }


            map[storageDetials] = mutableListOfPeople
            Log.i("medicine", "viewModel: map[${storageDetials.id}] = mutableListOfPeople ::: ${map[storageDetials]}")


        }

        return map
    }

    suspend fun updateSchedule() {
        scheduleRepository.updateSchedule(scheduleUiState.scheduleDetails.toSchedule(patientId))
        val scheduleId = scheduleUiState.scheduleDetails.id

       // val allterms = scheduleTermRepository.getAllsSchedulesTerms(scheduleId).filterNotNull().first()

        for(scheduleTermDetail in scheduleUiState.scheduleDetails.scheduleTermDetailsList)
        {
            scheduleTermRepository.updateScheduleTerm(scheduleTermDetail.toScheduleTerm(scheduleId))
        }

        val allterms = scheduleTermRepository.getAllsSchedulesTerms(scheduleId).filterNotNull().first().map { it.toScheduleTermDetails() }

        for (scheduleTermDetail in allterms)
        {
            if(!scheduleUiState.scheduleDetails.scheduleTermDetailsList.contains(scheduleTermDetail))
            {
                scheduleTermRepository.deleteScheduleTerm(scheduleTermDetail.toScheduleTerm(scheduleId))
            }
        }


        updateSchedulesInfo()
        //patientsSchedule.scheduleDetailsList + scheduleUiState.scheduleDetailsList
        scheduleUiState = ScheduleUiState()
    }


}



data class ScheduleUiState(val scheduleDetails: ScheduleDetails = ScheduleDetails())

data class StorageUiState(val storageDetails: StorageDetails = StorageDetails())


fun ScheduleDetails.toInfo(): String = "Lek: ${medicinDetails.name} \n" +
        "Przyjmowany jest w formie: ${medicinDetails.form} \n" +
        "Relacja leku z posiłkiem: ${medicinDetails.relation} \n" +
        "Początek kuracji: ${startDate}, koniec kuracji: ${endDate?: "nieznany"} \n" +
        "Terminy zażyć: \n" +
        "${scheduleTermDetailsList.map { "${it.day} - ${"%02d".format(it.hour)}:\${" +
                "                            \"%02d\".format(" +
                "                                it.minute" +
                "                            )  ${it.dose} x ${medicinDetails.form} \n" }}"


data class StorageDetails(
    val id: Int = 0,
    var MedicinId: Int =0,
    val quantity: Int = 0
)

fun StorageDetails.toStorage(): Storage = Storage(
    id=id,
    Medicineid =MedicinId,
    quantity = quantity
)

fun Storage.toStorageDetails(): StorageDetails = StorageDetails(
    id=id,
    MedicinId =  Medicineid,
    quantity =  quantity
)
/*
fun Schedule.toScheduleUiState(): ScheduleUiState = ScheduleUiState(
    scheduleDetails = this.toScheduleDetails()
)

 */

package com.example.medicin_app_v2.ui.zalecenia

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.medicin_app_v2.data.DayWeek
import com.example.medicin_app_v2.data.MealRelation
import com.example.medicin_app_v2.data.MedicinForm
import com.example.medicin_app_v2.data.firstAidKit.FirstAidKit
import com.example.medicin_app_v2.data.firstAidKit.FirstaidkitRepository
import com.example.medicin_app_v2.data.medicine.MedicinRepository
import com.example.medicin_app_v2.data.medicine.Medicine
import com.example.medicin_app_v2.data.patient.PatientsRepository
import com.example.medicin_app_v2.data.schedule.Schedule
import com.example.medicin_app_v2.data.schedule.ScheduleRepository
import com.example.medicin_app_v2.data.scheduleTerms.ScheduleTermRepository
import com.example.medicin_app_v2.data.storage.Storage
import com.example.medicin_app_v2.data.storage.StorageRepository
import com.example.medicin_app_v2.ui.PatientDetails
import com.example.medicin_app_v2.ui.PatientUiState
import com.example.medicin_app_v2.ui.home.MedicinDetails
import com.example.medicin_app_v2.ui.home.PatientScheduleDetailsInfo
import com.example.medicin_app_v2.ui.home.PatientScheduleInfo
import com.example.medicin_app_v2.ui.home.ScheduleDetails
import com.example.medicin_app_v2.ui.home.ScheduleTermDetails
import com.example.medicin_app_v2.ui.home.toMedicin
import com.example.medicin_app_v2.ui.home.toMedicinDetails
import com.example.medicin_app_v2.ui.home.toSchedule
import com.example.medicin_app_v2.ui.home.toScheduleDetails
import com.example.medicin_app_v2.ui.patients.PatientsDestination
import com.example.medicin_app_v2.ui.toPatientUiState
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import java.util.Date

class ZalecenieViewModel(
    savedStateHandle: SavedStateHandle,
    private val medicinRepository: MedicinRepository,
    private val storageRepository: StorageRepository,
    private val patientsRepository: PatientsRepository,
    private val scheduleRepository: ScheduleRepository,
    private val firstaidkitRepository: FirstaidkitRepository,
    private val scheduleTermRepository: ScheduleTermRepository
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

    }

    fun updatestorageState(storageDetails: StorageDetails)
    {
        storageUiState = StorageUiState(storageDetails)
    }

    suspend fun createStorage()
    {
        storageRepository.insertStorage(storageUiState.storageDetails.toStorage())
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


    suspend fun createSchedule()
    {
      //  Log.i("createSchedule", "start, listSize: ${scheduleUiState.scheduleDetailsList.size}")
     //  var medicinDetails = scheduleUiState.scheduleDetailsList.first().medicinDetails
        var medicinDetails = scheduleUiState.scheduleDetails.medicinDetails
        Log.i("createSchedule", "medicinDetails: ${medicinDetails.name}")

        val medicineinDB = medicinRepository.getMedicineStream(
            medicinDetails.name, medicinDetails.form).first()
        Log.i("createSchedule", "medicininDB: ${medicineinDB?.id}")
        var medicinId : Int
        if(medicineinDB==null)
        {
            Log.i("createSchedule", "medicininDB: puste")
           // medicinRepository.insertMedicine(medicinDetails.toMedicin())
          //  medicinId = medicinRepository.getAllMedicinesStream().first().size
            medicinId = medicinRepository.insertMedicine(medicinDetails.toMedicin()).toInt()
        }
        else
        {
            medicinId = medicineinDB.id
        }
        Log.i("createSchedule", "medID: $medicinId")

        storageUiState.storageDetails.MedicinId = medicinId
        val idxStorage = storageRepository.insertStorage(storageUiState.storageDetails.toStorage()).toInt()
        Log.i("createSchedule", "storageCreated:")
       // val idxStorage = storageRepository.getAllStoragesStream().filterNotNull().first().size

        firstaidkitRepository.insertFirstAidKit(FirstAidKit(Patient_id= patientId, Storage_id =idxStorage ))

        scheduleUiState.scheduleDetails.medicinDetails.id= medicinId
        val scheduleId = scheduleRepository.insertSchedule( scheduleUiState.scheduleDetails.toSchedule(patientId)).toInt()

        for(scheduleTermDetail in scheduleUiState.scheduleDetails.scheduleTermDetailsList)
        {
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


    suspend fun getMedicinesStorage(medicinDetails: MedicinDetails) : MutableMap< StorageDetails, MutableList<String>>
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



}


//fun PatientScheduleDetailsInfo.toMedicinScheduleInfoList() : List<ScheduleDetails>
//{
//    Log.i("listManipulation: ", "start conversion")
//    Log.i("listManipulation: ", "size list details=  ${this.scheduleDetailsList.size}")
//
//    var medicinScheduleInfoList : List<ScheduleDetails> =listOf()
//   // return medicinScheduleInfoList
//    if(this.scheduleDetailsList.isNotEmpty()) {
//        Log.i("listManipulation: ", "nie jest empty ale jakos dalej nie widzie")
//        for (scheduleDetail in this.scheduleDetailsList) {
//            Log.i("listManipulation: ", "wywala przez fora")
//            var medicinScheduleInfo =
//                medicinScheduleInfoList.find { it.medicinDetails.id == scheduleDetail.medicinDetails.id }
//            if (medicinScheduleInfo == null) {
//
//                medicinScheduleInfo = ScheduleDetails(
//                    id = scheduleDetail.id,
//                    medicinDetails = scheduleDetail.medicinDetails ,
//                    startDate = scheduleDetail.startDate,
//                    endDate = scheduleDetail.endDate,
//                    scheduleTermDetailsList = scheduleDetail.scheduleTermDetailsList
//                )
//
//                medicinScheduleInfoList =medicinScheduleInfoList+ medicinScheduleInfo
//            }
//
//        }
//    }
//
//    Log.i("listManipulation: ", "wielkosc nowego ${medicinScheduleInfoList.size}")
//
//    return medicinScheduleInfoList
//}
//



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

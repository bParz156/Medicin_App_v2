package com.example.medicin_app_v2.ui.zalecenia

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.medicin_app_v2.data.medicine.MedicinRepository
import com.example.medicin_app_v2.data.medicine.Medicine
import com.example.medicin_app_v2.data.patient.PatientsRepository
import com.example.medicin_app_v2.data.schedule.Schedule
import com.example.medicin_app_v2.data.schedule.ScheduleRepository
import com.example.medicin_app_v2.data.storage.Storage
import com.example.medicin_app_v2.data.storage.StorageRepository
import com.example.medicin_app_v2.ui.PatientDetails
import com.example.medicin_app_v2.ui.PatientUiState
import com.example.medicin_app_v2.ui.home.PatientScheduleDetailsInfo
import com.example.medicin_app_v2.ui.home.PatientScheduleInfo
import com.example.medicin_app_v2.ui.home.ScheduleDetails
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

class ZalecenieViewModel(
    savedStateHandle: SavedStateHandle,
    private val medicinRepository: MedicinRepository,
    private val storageRepository: StorageRepository,
    private val patientsRepository: PatientsRepository,
    private val scheduleRepository: ScheduleRepository
) : ViewModel()
{

    private val patientId : Int = try{checkNotNull(savedStateHandle[PatientsDestination.patientIdArg])}
    catch (e:IllegalStateException)
    {
        -1
    }
    var patientUiState by mutableStateOf(PatientUiState())
        private set


    var patientsSchedule by mutableStateOf(PatientScheduleDetailsInfo())
        private set

    var scheduleUiState by mutableStateOf(ScheduleUiState())
        private  set

    var storageUiState by mutableStateOf(StorageUiState())
        private set

    init {
        viewModelScope.launch {

            patientUiState = patientsRepository.getPatientStream(patientId)
                .filterNotNull()
                .first()
                .toPatientUiState()

            val Schedules = scheduleRepository.getAllPatientsSchedules(patientId)
                .first() // Poczekaj na pierwszy wynik z Flow
                .let { PatientScheduleInfo(it) } // Zmapuj wynik na PatientScheduleInfo

            patientsSchedule = PatientScheduleDetailsInfo(
                Schedules.scheduleList.map { it.toScheduleDetails(
                    medicinDetails = medicinRepository.getMedicineStream(it.Medicine_id)
                        .filterNotNull()
                        .first()
                        .toMedicinDetails()
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
        scheduleUiState = ScheduleUiState(scheduleDetails)
    }

    suspend fun deleteSchedule()
    {
        scheduleRepository.deleteSchedule(scheduleUiState.scheduleDetails.toSchedule(patientId))
    }

    suspend fun createSchedule()
    {
        createMedicin()
        scheduleRepository.insertSchedule(scheduleUiState.scheduleDetails.toSchedule(patientId))
    }

    suspend fun createMedicin()
    {
        Log.i("zalecenia", "createMedicin")

        val medicineinDB = medicinRepository.getMedicineStream(
            scheduleUiState.scheduleDetails.medicinDetails.name,
            scheduleUiState.scheduleDetails.medicinDetails.form).first()
        if(medicineinDB == null)
        {
            val idx = medicinRepository.getAllMedicinesStream().first().size
            medicinRepository.insertMedicine(scheduleUiState.scheduleDetails.medicinDetails.toMedicin())
            storageUiState.storageDetails.MedicinId = if(idx==0) idx+1 else idx
            storageRepository.insertStorage(storageUiState.storageDetails.toStorage())
        }
        else
        {
            storageUiState.storageDetails.MedicinId = medicineinDB.id
            storageRepository.updateStorage(storageUiState.storageDetails.toStorage())

        }



    }


    fun getPatientsName(): String{
        return patientUiState.patientDetails.name
    }

}



data class ScheduleUiState(val scheduleDetails: ScheduleDetails = ScheduleDetails())

data class StorageUiState(val storageDetails: StorageDetails = StorageDetails())

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

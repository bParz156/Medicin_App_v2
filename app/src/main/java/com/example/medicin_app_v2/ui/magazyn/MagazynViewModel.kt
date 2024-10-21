package com.example.medicin_app_v2.ui.magazyn

import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.medicin_app_v2.data.DayWeek
import com.example.medicin_app_v2.data.MedicinForm
import com.example.medicin_app_v2.data.firstAidKit.FirstAidKit
import com.example.medicin_app_v2.data.firstAidKit.FirstaidkitRepository
import com.example.medicin_app_v2.data.medicine.MedicinRepository
import com.example.medicin_app_v2.data.patient.PatientsRepository
import com.example.medicin_app_v2.data.schedule.ScheduleRepository
import com.example.medicin_app_v2.data.scheduleTerms.ScheduleTermRepository
import com.example.medicin_app_v2.data.storage.Storage
import com.example.medicin_app_v2.data.storage.StorageRepository
import com.example.medicin_app_v2.ui.PatientDetails
import com.example.medicin_app_v2.ui.PatientUiState
import org.apache.commons.collections4.queue.CircularFifoQueue
import com.example.medicin_app_v2.ui.home.HomeDestination
import com.example.medicin_app_v2.ui.home.PatientScheduleDetailsInfo
import com.example.medicin_app_v2.ui.home.PatientScheduleInfo
import com.example.medicin_app_v2.ui.home.ScheduleTermDetails
import com.example.medicin_app_v2.ui.home.toMedicinDetails
import com.example.medicin_app_v2.ui.home.toScheduleDetails
import com.example.medicin_app_v2.ui.home.toScheduleTermDetails
import com.example.medicin_app_v2.ui.toPatientUiState
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.util.Calendar
import java.util.Date

class MagazynViewModel (
    savedStateHandle: SavedStateHandle,
 patientsRepository: PatientsRepository,
 scheduleRepository: ScheduleRepository,
 medicinRepository: MedicinRepository,
    private val storageRepository: StorageRepository,
    firstaidkitRepository: FirstaidkitRepository,
    private val scheduleTermRepository: ScheduleTermRepository
) : ViewModel()
{

    var magazynUiState by mutableStateOf(ZaleceniaUiState())
        private set


    private val patientId : Int = try{checkNotNull(savedStateHandle[HomeDestination.patientIdArg])}
    catch (e:IllegalStateException)
    {
        -1
    }


    private var storageDetailsList: List<StorageDetails> = listOf()


    init {
        viewModelScope.launch {

            val Schedules = scheduleRepository.getAllPatientsSchedules(patientId)
                .first() // Poczekaj na pierwszy wynik z Flow
                .let { PatientScheduleInfo(it) } // Zmapuj wynik na PatientScheduleInfo

            val firstAidKits = firstaidkitRepository.getAllFirstAidKitsStream(patientId)
                .first()


            for(kit in firstAidKits)
            {
                val storage = storageRepository.getStorageStream(kit.Storage_id).filterNotNull().first()
                storageDetailsList = storageDetailsList + StorageDetails(
                    storageId = storage.id,
                    medicinId =  storage.Medicineid,
                    quantity = storage.quantity,
                    medName = medicinRepository.getMedicineStream(storage.Medicineid).filterNotNull().first().name,
                   // daysToEnd = 0,
                    daysToEnd = calculateDaysToEnd(
                        scheduleTermDetailsList =  scheduleTermRepository.getAllsSchedulesTerms(
                            scheduleId = scheduleRepository.getPatientMedicineSchedule(patient_id = patientId , medicine_id = storage.Medicineid ).filterNotNull().first().id
                        ).filterNotNull().first().map { it.toScheduleTermDetails() }
                        ,
                        quantity = storage.quantity
                    ),
                    medicinForm = medicinRepository.getMedicineStream(storage.Medicineid).filterNotNull().first().form
                )
            }

            magazynUiState = ZaleceniaUiState(storageDetailsList = storageDetailsList,
                patientDetails =  PatientDetails(id= patientId,
                    name =  patientsRepository.getPatientStream(patientId).filterNotNull().first().name)
            )


        }
    }

    private fun calculateDaysToEnd(scheduleTermDetailsList: List<ScheduleTermDetails>, quantity: Int) : Int
    {
        //DO POPRAWYYY
        val calendar = Calendar.getInstance()
        var dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK)
        var toEnd =0
        var stopPit: Boolean = false
        val usageDay: List<Int> =calculateWeeklyUsage(scheduleTermDetailsList)
        Log.i("calday", "${DayWeek.values().find{it.weekDay==1}?.name}  ${usageDay[0]}")
        Log.i("calday", "${DayWeek.values().find{it.weekDay==5}?.name}  ${usageDay[4]}")
        var used =0
        while(used<=quantity && !stopPit)
        {
            Log.i("calday", "while: used = ${used} , toEnd= ${toEnd}  dayOfWeek =${dayOfWeek}")
            if(usageDay[dayOfWeek]+ used > quantity)
                stopPit = true
            else {
                used += usageDay[dayOfWeek]
                dayOfWeek = (dayOfWeek + 1) % 7
                toEnd++
            }

        }
        Log.i("calday", "nie wychodzi z while")

        return toEnd
    }

    private fun calculateWeeklyUsage(scheduleTermDetailsList: List<ScheduleTermDetails>): List<Int>
    {
        val list = MutableList(7) { 0 }
        for(day in DayWeek.values())
        {
            list[day.weekDay-1] = calculateDayUsage(scheduleTermDetailsList, day)
            Log.i("calday", "${day.name}  -- ${list[day.weekDay -1]}")
        }
        return list
    }

    private fun calculateDayUsage(scheduleTermDetailsList: List<ScheduleTermDetails>, dayWeek: DayWeek): Int
    {
        var uasage: Int =0
        for ( scheduleTermDetail in scheduleTermDetailsList)
        {
            if(scheduleTermDetail.day == dayWeek)
            {
                uasage+=scheduleTermDetail.dose
            }

        }

        return  uasage
    }


    fun getPatientsName(): String{
        return magazynUiState.patientDetails.name
    }


    fun increaseStorageQuantity()
    {
        storageDetailsList - magazynUiState.changingStoragDetails
        Log.i("magazyn", "in increase")
        viewModelScope.launch {
            val currentStorage =magazynUiState.changingStoragDetails.toStorage()
            storageRepository.updateStorage(currentStorage)
        }
        storageDetailsList + magazynUiState.changingStoragDetails

    }

    fun updateUiState(storageDetails: StorageDetails) {
        Log.i("magazyn", "in updateUI : ${magazynUiState.changingStoragDetails.medName}")
        magazynUiState.changingStoragDetails = storageDetails
        Log.i("magazyn", "in updateUI : ${magazynUiState.changingStoragDetails.medName}")

    }


}


data class ZaleceniaUiState(
    val storageDetailsList: List<StorageDetails> = listOf(),
    val patientDetails: PatientDetails = PatientDetails(),
    var changingStoragDetails: StorageDetails = StorageDetails()
)

data class StorageDetails(
    val storageId: Int =0,
    val medicinId: Int =0,
    val quantity: Int =0,
    val medName: String = "",
    val daysToEnd: Int =0,
    val medicinForm: MedicinForm =MedicinForm.TABLETKA
)

fun StorageDetails.toStorage() : Storage = Storage(
    id = this.storageId,
    Medicineid = this.medicinId,
    quantity =  this.quantity
)

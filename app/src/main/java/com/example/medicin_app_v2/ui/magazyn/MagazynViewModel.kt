package com.example.medicin_app_v2.ui.magazyn

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.medicin_app_v2.data.MedicinForm
import com.example.medicin_app_v2.data.firstAidKit.FirstAidKit
import com.example.medicin_app_v2.data.firstAidKit.FirstaidkitRepository
import com.example.medicin_app_v2.data.medicine.MedicinRepository
import com.example.medicin_app_v2.data.patient.PatientsRepository
import com.example.medicin_app_v2.data.schedule.ScheduleRepository
import com.example.medicin_app_v2.data.storage.Storage
import com.example.medicin_app_v2.data.storage.StorageRepository
import com.example.medicin_app_v2.ui.PatientDetails
import com.example.medicin_app_v2.ui.PatientUiState
import com.example.medicin_app_v2.ui.home.HomeDestination
import com.example.medicin_app_v2.ui.home.PatientScheduleDetailsInfo
import com.example.medicin_app_v2.ui.home.PatientScheduleInfo
import com.example.medicin_app_v2.ui.home.toMedicinDetails
import com.example.medicin_app_v2.ui.home.toScheduleDetails
import com.example.medicin_app_v2.ui.toPatientUiState
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.util.Calendar
import java.util.Date

class MagazynViewModel (
    savedStateHandle: SavedStateHandle,
 patientsRepository: PatientsRepository,
 scheduleRepository: ScheduleRepository,
 medicinRepository: MedicinRepository,
    storageRepository: StorageRepository,
    firstaidkitRepository: FirstaidkitRepository
) : ViewModel()
{

    var homeUiState by mutableStateOf(ZaleceniaUiState())
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
                    daysToEnd = calculateDaysToEnd(
                        dose = scheduleRepository.getPatientMedicineSchedule(patientId, storage.Medicineid).filterNotNull().first().get(0).dose,
                        timesAWeek =scheduleRepository.getPatientMedicineSchedule(patientId, storage.Medicineid).filterNotNull().first().size,
                        quantity = storage.quantity
                    ),
                    medicinForm = medicinRepository.getMedicineStream(storage.Medicineid).filterNotNull().first().form
                )
            }

            homeUiState = ZaleceniaUiState(storageDetailsList = storageDetailsList,
                patientDetails =  PatientDetails(id= patientId,
                    name =  patientsRepository.getPatientStream(patientId).filterNotNull().first().name)
            )


        }
    }

    private fun calculateDaysToEnd(dose: Int, timesAWeek: Int, quantity: Int) : Int
    {
        //DO POPRAWYYY
        return 7*quantity/(dose*timesAWeek)
    }

    fun getPatientsName(): String{
        return homeUiState.patientDetails.name
    }

}


data class ZaleceniaUiState(
    val storageDetailsList: List<StorageDetails> = listOf(),
    val patientDetails: PatientDetails = PatientDetails()
)

data class StorageDetails(
    val storageId: Int,
    val medicinId: Int,
    val quantity: Int,
    val medName: String = "",
    val daysToEnd: Int,
    val medicinForm: MedicinForm
)

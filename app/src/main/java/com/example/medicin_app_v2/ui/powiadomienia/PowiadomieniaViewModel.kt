package com.example.medicin_app_v2.ui.powiadomienia

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.medicin_app_v2.data.UserPreferencesRepository
import com.example.medicin_app_v2.data.WorkerRepository
import com.example.medicin_app_v2.data.medicine.MedicinRepository
import com.example.medicin_app_v2.data.notification.Notification
import com.example.medicin_app_v2.data.patient.PatientsRepository
import com.example.medicin_app_v2.data.schedule.ScheduleRepository
import com.example.medicin_app_v2.data.scheduleTerms.ScheduleTermRepository
import com.example.medicin_app_v2.data.usage.UsageRepository
import com.example.medicin_app_v2.ui.PatientUiState
import com.example.medicin_app_v2.ui.patients.PatientsDestination
import com.example.medicin_app_v2.ui.toPatientUiState
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class PowiadomieniaViewModel (
    savedStateHandle: SavedStateHandle,
    patientsRepository: PatientsRepository,

) : ViewModel() {
   // var notificationUiState by mutableStateOf(NotificationUiState())
   //     private set

    private val patientId : Int = try{checkNotNull(savedStateHandle[PowiadomieniaDestination.patientIdArg])}
    catch (e:IllegalStateException)
    {
        -1
    }
    var patientUiState by mutableStateOf(PatientUiState())
        private set

    init {
        viewModelScope.launch {

            patientUiState = patientsRepository.getPatientStream(patientId)
                .filterNotNull()
                .first()
                .toPatientUiState()
        }
    }

    fun getPatientsName(): String{
        return patientUiState.patientDetails.name
    }
}

data class NotificationUiState(
    var notificationList: MutableList<Notification> = mutableListOf()
)
package com.example.medicin_app_v2.ui.powiadomienia

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.medicin_app_v2.data.examination.Examination
import com.example.medicin_app_v2.data.examination.ExaminationRepository
import com.example.medicin_app_v2.data.examination.ExaminationType
import com.example.medicin_app_v2.data.patient.PatientsRepository
import com.example.medicin_app_v2.ui.PatientUiState
import com.example.medicin_app_v2.ui.toPatientUiState
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class PowiadomieniaViewModel (
    savedStateHandle: SavedStateHandle,
    patientsRepository: PatientsRepository,
    private val examinationRepository: ExaminationRepository

) : ViewModel() {
    var notificationUiState by mutableStateOf(NotificationUiState())
        private set


    private val patientId : Int = try{checkNotNull(savedStateHandle[PowiadomieniaDestination.patientIdArg])}
    catch (e:IllegalStateException)
    {
        -1
    }

    init {
        Log.i("Badania", "init: ${notificationUiState.listExamination}")
        viewModelScope.launch {

            val patientUiState = patientsRepository.getPatientStream(patientId)
                .filterNotNull()
                .first()
                .toPatientUiState()

            val listExamination = examinationRepository.getPatientsExaminations(patientId).filterNotNull().first()
            Log.i("Badania", "init: ${notificationUiState.listExamination}")

            notificationUiState = NotificationUiState(
                patientUiState = patientUiState,
                listExamination = listExamination
            )
        }
    }

    fun getPatientsName(): String{
        return notificationUiState.patientUiState.patientDetails.name
    }

    suspend fun createExamination()
    {
        examinationRepository.insertExamination(examination = notificationUiState.examination )
    }

    fun updateUiState(examination: Examination)
    {
        examination.Patient_id = patientId
        notificationUiState.examination = examination
    }


    suspend fun getExaminationOfTypes(lisTypes: List<ExaminationType>)
    {
        notificationUiState.listExamination = listOf()
        for(type in lisTypes)
        {
            notificationUiState.listExamination += examinationRepository.getPatientsExaminationsType(patientId, type).filterNotNull().first()
        }
    }

    suspend fun deleteExamination()
    {
        examinationRepository.deleteExamination(notificationUiState.examination)
    }


}

data class NotificationUiState(
    var patientUiState: PatientUiState = PatientUiState(),
    var listExamination: List<Examination> = listOf(),
    var examination: Examination = Examination()
)

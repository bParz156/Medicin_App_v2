package com.example.medicin_app_v2.data.usage


import com.example.medicin_app_v2.data.patient.Patient
import com.example.medicin_app_v2.data.schedule.Schedule
import kotlinx.coroutines.flow.Flow

interface UsageRepository {


    suspend fun insert(usage: Usage)

    suspend fun update(usage: Usage)

    suspend fun delete(usage: Usage)

    fun getPatientMedcicineUsage(usage_id: Int): Flow<Usage>

    fun getAllPatientsUsages(schedule_id: Int): Flow<List<Usage>>
}
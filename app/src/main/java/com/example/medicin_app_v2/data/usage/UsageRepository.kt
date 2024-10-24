package com.example.medicin_app_v2.data.usage


import com.example.medicin_app_v2.data.patient.Patient
import com.example.medicin_app_v2.data.schedule.Schedule
import kotlinx.coroutines.flow.Flow
import java.util.Date

interface UsageRepository {


    suspend fun insert(usage: Usage) : Long

    suspend fun update(usage: Usage)

    suspend fun delete(usage: Usage)

    suspend fun deleteOld(expiryDate: Date)

    fun getPatientMedcicineUsage(usage_id: Int): Flow<Usage>

    fun getAllScheduleTermUsages(scheduleTerm_id: Int): Flow<List<Usage>>

    fun getUsage(scheduleTerm_id: Int, useDate: Date): Flow<Usage>


}
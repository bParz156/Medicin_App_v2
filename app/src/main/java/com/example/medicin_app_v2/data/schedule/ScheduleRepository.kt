package com.example.medicin_app_v2.data.schedule

import kotlinx.coroutines.flow.Flow

interface ScheduleRepository {

    fun getAllPatientsSchedules(patient_id : Int): Flow<List<Schedule>>

    fun getPatientMedicineSchedule(patient_id: Int, medicine_id: Int) : Flow<Schedule>

    fun getScheduleById(id: Int) : Flow<Schedule>

    suspend fun insertSchedule(schedule: Schedule)

    suspend fun deleteSchedule(schedule: Schedule)

    suspend fun updateSchedule(schedule: Schedule)

    suspend fun getSizeId(): Int

}
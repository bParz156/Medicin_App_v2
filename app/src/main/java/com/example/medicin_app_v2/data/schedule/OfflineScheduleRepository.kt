package com.example.medicin_app_v2.data.schedule

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first

class OfflineScheduleRepository(private val scheduleDao: ScheduleDao) : ScheduleRepository {

    override fun getAllPatientsSchedules(patient_id: Int): Flow<List<Schedule>> {
        return scheduleDao.getAllPatientsSchedules(patient_id)
    }

    override fun getPatientMedicineSchedule(
        patient_id: Int,
        medicine_id: Int
    ): Flow<Schedule> {
        return scheduleDao.getPatientMedcicineSchedule(patient_id, medicine_id)
    }

    override fun getScheduleById(id: Int): Flow<Schedule> {
        return scheduleDao.getScheduleById(id)
    }

    override fun getAllSchedules(): Flow<List<Schedule>> {
        return scheduleDao.getAllSchedules()
    }

    override suspend fun insertSchedule(schedule: Schedule) : Long {
        return scheduleDao.insert(schedule)
    }

    override suspend fun deleteSchedule(schedule: Schedule) {
        scheduleDao.delete(schedule)
    }

    override suspend fun updateSchedule(schedule: Schedule) {
        scheduleDao.update(schedule)
    }

}
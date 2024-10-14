package com.example.medicin_app_v2.data.usage

import com.example.medicin_app_v2.data.patient.Patient
import com.example.medicin_app_v2.data.schedule.Schedule
import kotlinx.coroutines.flow.Flow

class OfflineUsageRepository (private val usageDao: UsageDao) : UsageRepository {
    override suspend fun insert(usage: Usage) {
        usageDao.insert(usage)
    }

    override suspend fun update(usage: Usage) {
        usageDao.update(usage)
    }

    override suspend fun delete(usage: Usage) {
        usageDao.delete(usage)
    }

    override fun getPatientMedcicineUsage(usage_id: Int): Flow<Usage> {
        return usageDao.getPatientMedcicineSchedule(usage_id)
    }

    override fun getAllPatientsUsages(schedule_id: Int): Flow<List<Usage>> {
        return usageDao.getAllPatientsSchedules(schedule_id)
    }
}
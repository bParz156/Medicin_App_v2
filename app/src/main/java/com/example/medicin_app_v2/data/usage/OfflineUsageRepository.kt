package com.example.medicin_app_v2.data.usage

import com.example.medicin_app_v2.data.patient.Patient
import com.example.medicin_app_v2.data.schedule.Schedule
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.count
import java.util.Date

class OfflineUsageRepository (private val usageDao: UsageDao) : UsageRepository {
    override suspend fun insert(usage: Usage) : Long {
        return usageDao.insert(usage)
    }

    override suspend fun update(usage: Usage) {
        usageDao.update(usage)
    }

    override suspend fun delete(usage: Usage) {
        usageDao.delete(usage)
    }

    override suspend fun deleteOld(expiryDate: Date) {
        usageDao.deleteOldEvents(expiryDate)
    }

    override fun getPatientMedcicineUsage(usage_id: Int): Flow<Usage> {
        return usageDao.getPatientMedcicineSchedule(usage_id)
    }

    override fun getAllScheduleTermUsages(scheduleTerm_id: Int): Flow<List<Usage>> {
        return usageDao.getAllSchedulesTermUsage(scheduleTerm_id)
    }

    override fun getUsage(scheduleTerm_id: Int, useDate: Date): Flow<Usage?> {
        return usageDao.getUsage(scheduleTerm_id =scheduleTerm_id, useDate = useDate)
    }


}
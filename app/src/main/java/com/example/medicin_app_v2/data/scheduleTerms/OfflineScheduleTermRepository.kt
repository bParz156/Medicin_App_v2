package com.example.medicin_app_v2.data.scheduleTerms

import kotlinx.coroutines.flow.Flow

class OfflineScheduleTermRepository(private val scheduleTermDao: ScheduleTermDao): ScheduleTermRepository{
    override fun getScheduleTermStream(id: Int): Flow<ScheduleTerm?> {
        return scheduleTermDao.getScheduleTermById(id)
    }

    override fun getAllsSchedulesTerms(scheduleId: Int): Flow<List<ScheduleTerm>> {
        return scheduleTermDao.getScheduleTermBySchedule(scheduleId)
    }

    override suspend fun insertScheduleTerm(scheduleTerm: ScheduleTerm) {
        scheduleTermDao.insert(scheduleTerm)
    }

    override suspend fun deleteScheduleTerm(scheduleTerm: ScheduleTerm) {
        scheduleTermDao.delete(scheduleTerm)
    }

    override suspend fun updateScheduleTerm(scheduleTerm: ScheduleTerm) {
        scheduleTermDao.update(scheduleTerm)
    }

}
package com.example.medicin_app_v2.data.scheduleTerms

import kotlinx.coroutines.flow.Flow

interface ScheduleTermRepository {

    fun getScheduleTermStream(id: Int) : Flow<ScheduleTerm?>

    fun getAllsSchedulesTerms(scheduleId: Int): Flow<List<ScheduleTerm>>


    suspend fun insertScheduleTerm(scheduleTerm: ScheduleTerm)

    /**
     * Delete ScheduleTerm from the data source
     */
    suspend fun deleteScheduleTerm(scheduleTerm: ScheduleTerm)

    /**
     * Update ScheduleTerm in the data source
     */
    suspend fun updateScheduleTerm(scheduleTerm: ScheduleTerm)

}
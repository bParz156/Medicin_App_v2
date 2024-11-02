package com.example.medicin_app_v2.data.scheduleTerms

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow


@Dao
interface ScheduleTermDao
{
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(scheduleTerm: ScheduleTerm) : Long

    @Update
    suspend fun update(scheduleTerm: ScheduleTerm)

    @Delete
    suspend fun delete(scheduleTerm: ScheduleTerm)

    @Query("SELECT *from ScheduleTerm where id = :id")
    fun getScheduleTermById(id: Int) : Flow<ScheduleTerm>


    @Query("SELECT *from ScheduleTerm where ScheduleId = :scheduleId")
    fun getScheduleTermBySchedule(scheduleId: Int) : Flow<List<ScheduleTerm>>


}
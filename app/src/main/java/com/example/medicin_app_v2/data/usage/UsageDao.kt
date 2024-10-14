package com.example.medicin_app_v2.data.usage

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.medicin_app_v2.data.patient.Patient
import com.example.medicin_app_v2.data.schedule.Schedule
import kotlinx.coroutines.flow.Flow


@Dao
interface UsageDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(usage: Usage)

    @Update
    suspend fun update(usage: Usage)

    @Delete
    suspend fun delete(usage: Usage)

    @Query("SELECT * from usage WHERE id = :usage_id")
    fun getPatientMedcicineSchedule(usage_id: Int): Flow<Usage>

    @Query("SELECT * from usage WHERE Schedule_id = :schedule_id")
    fun getAllPatientsSchedules(schedule_id: Int): Flow<List<Usage>>

}
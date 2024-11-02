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
import java.util.Date


@Dao
interface UsageDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(usage: Usage) : Long

    @Update
    suspend fun update(usage: Usage)

    @Delete
    suspend fun delete(usage: Usage)

    @Query("SELECT * from usage WHERE id = :usage_id")
    fun getPatientMedcicineSchedule(usage_id: Int): Flow<Usage>

    @Query("SELECT * from usage WHERE ScheduleTerm_id = :scheduleTerm_id")
    fun getAllSchedulesTermUsage(scheduleTerm_id: Int): Flow<List<Usage>>

    @Query("DELETE from usage WHERE date< :expiryDate")
    fun deleteOldEvents(expiryDate: Date)

    @Query("Select * from usage")
    fun getAllUsages(): Flow<List<Usage>>

    @Query("select * from usage where ScheduleTerm_id = :scheduleTerm_id and date = :useDate")
    fun getUsage(scheduleTerm_id: Int, useDate: Date): Flow<Usage?>




}
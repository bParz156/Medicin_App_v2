package com.example.medicin_app_v2.data.schedule

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.medicin_app_v2.data.patient.Patient
import kotlinx.coroutines.flow.Flow
import java.util.Date


@Dao
interface ScheduleDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(schedule: Schedule) : Long

    @Update
    suspend fun update(schedule: Schedule)

    @Delete
    suspend fun delete(schedule: Schedule)

    @Query("SELECT * from schedule WHERE id= :id")
    fun getScheduleById(id: Int): Flow<Schedule>


    @Query("SELECT * from schedule WHERE Patient_id = :patient_id and Medicine_id = :medicine_id")
    fun getPatientMedcicineSchedule(patient_id: Int, medicine_id: Int): Flow<Schedule>

    @Query("SELECT * from schedule WHERE Patient_id = :patient_id")
    fun getAllPatientsSchedules(patient_id: Int): Flow<List<Schedule>>


    @Query("Select * from schedule")
    fun getAllSchedules(): Flow<List<Schedule>>

    @Query("Select * from schedule where endDate!= null and endDate<:today and Medicine_id = :medicine_id")
    fun getActiveSchedulesByMedicine(today: Date, medicine_id: Int): Flow<List<Schedule>>

}
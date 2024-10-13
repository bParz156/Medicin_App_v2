package com.example.medicin_app_v2.data.schedule

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.medicin_app_v2.data.patient.Patient
import kotlinx.coroutines.flow.Flow


@Dao
interface ScheduleDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(schedule: Schedule)

    @Update
    suspend fun update(schedule: Schedule)

    @Delete
    suspend fun delete(schedule: Schedule)

    @Query("SELECT * from schedule WHERE id= :id")
    fun getScheduleById(id: Int): Flow<Schedule>


    @Query("SELECT * from schedule WHERE Patient_id = :patient_id and Medicine_id = :medicine_id")
    fun getPatientMedcicineSchedule(patient_id: Int, medicine_id: Int): Flow<Schedule>

    @Query("SELECT * from schedule WHERE Patient_id = :patient_id")
    fun getAllPatientsSchedules(patient_id: Int): Flow<List<Patient>>

}
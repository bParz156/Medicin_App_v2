package com.example.medicin_app_v2.data.patient

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface PatientDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(patient: Patient) : Long

    @Update
    suspend fun update(patient: Patient)

    @Delete
    suspend fun delete(patient: Patient)

    @Query("SELECT * from patients WHERE id = :id")
    fun getpatient(id: Int): Flow<Patient>

    @Query("SELECT * from patients ORDER BY name ASC")
    fun getAllpatients(): Flow<List<Patient>>

    @Query("SELECT * from patients WHERE name = :name")
    fun getPatientByName(name: String) : Flow<Patient>

}
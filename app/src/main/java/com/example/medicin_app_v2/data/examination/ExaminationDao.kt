package com.example.medicin_app_v2.data.examination

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

/**
 * Interfejs DataAccess Operation dla encji Examination
 */
@Dao
interface ExaminationDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(examination: Examination) : Long

    @Update
    suspend fun update(examination: Examination)

    @Delete
    suspend fun delete(examination: Examination)

    @Query("Select * from examination where id=:id")
    fun getExaminationById(id: Int): Flow<List<Examination>>

    /**
     * Zwraca wszystkie badania pacjenta o wskazanym id) w postaci listy
     */
    @Query("Select * from examination where Patient_id = :patient_id")
    fun getPatientsExaminations(patient_id: Int) : Flow<List<Examination>>

    /**
     * Zwraca  badania o wskazanym typie pacjenta o wskazanym id) w postaci listy
     */
    @Query("Select * from examination where Patient_id = :patient_id and type = :type")
    fun getPatientsExaminationsType(patient_id: Int, type: ExaminationType) : Flow<List<Examination>>
}
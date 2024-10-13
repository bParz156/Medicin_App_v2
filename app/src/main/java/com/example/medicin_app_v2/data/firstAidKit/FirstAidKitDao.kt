package com.example.medicin_app_v2.data.firstAidKit

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow


@Dao
interface FirstAidKitDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(firstAidKit: FirstAidKit)

    @Update
    suspend fun update(firstAidKit: FirstAidKit)

    @Delete
    suspend fun delete(firstAidKit: FirstAidKit)

    @Query("SELECT * from FirstAidKit WHERE Patient_id = :patients_id")
    fun getpatientsfirstAidKit(patients_id: Int): Flow<List<FirstAidKit>>



}
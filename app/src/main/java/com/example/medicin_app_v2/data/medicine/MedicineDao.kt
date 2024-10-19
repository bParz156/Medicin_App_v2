package com.example.medicin_app_v2.data.medicine

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.medicin_app_v2.data.MedicinForm
import kotlinx.coroutines.flow.Flow

@Dao
interface MedicineDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(medicine: Medicine)

    @Update
    suspend fun update(medicine: Medicine)

    @Delete
    suspend fun delete(medicine: Medicine)

    @Query("SELECT * from medicines WHERE id = :id")
    fun getmedicine(id: Int): Flow<Medicine>

    @Query("SELECT * from medicines WHERE name= :name and form = :form")
    fun getmedicine(name:String, form: MedicinForm) : Flow<Medicine>

    @Query("SELECT * from medicines ORDER BY name ASC")
    fun getAllmedicines(): Flow<List<Medicine>>

}
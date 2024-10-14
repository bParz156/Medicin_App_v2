package com.example.medicin_app_v2.data.storage

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface StorageDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(storage: Storage)

    @Update
    suspend fun update(storage: Storage)

    @Delete
    suspend fun delete(storage: Storage)

    @Query("SELECT * from storage WHERE id = :id")
    fun getStorage(id: Int): Flow<Storage>


    @Query("SELECT * from storage ORDER BY Medicineid ASC")
    fun getAllStorages(): Flow<List<Storage>>
}
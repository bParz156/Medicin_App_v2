package com.example.medicin_app_v2.data.notification

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface NotificationDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(notification: Notification)

    @Update
    suspend fun update(notification: Notification)

    @Delete
    suspend fun delete(notification: Notification)

    @Query("SELECT * from notification WHERE id = :id")
    fun getNotification(id: Int): Flow<Notification>

    @Query("SELECT * from notification where Usageid = :usage_id order by date DESC")
    fun getAllnotificationABoutUsage(usage_id: Int): Flow<List<Notification>>

    @Query("SELECT * from notification  order by date DESC")
    fun getAllnotification(): Flow<List<Notification>>
}
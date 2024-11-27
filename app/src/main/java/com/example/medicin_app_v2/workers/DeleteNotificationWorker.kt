package com.example.medicin_app_v2.workers

import android.content.Context
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.medicin_app_v2.data.AppDatabase
import java.util.Calendar

class DeleteNotificationWorker(
    ctx: Context,
    params: WorkerParameters
): CoroutineWorker(ctx, params) {

    private val notificationDao  = AppDatabase.getDatabase(ctx).notificationDao()
    override suspend fun doWork(): Result {

        return try{
            deleteNotifications()
            Result.success()
        }catch (e: Exception) {
            Result.failure()
        }
    }



    fun deleteNotifications()
    {
        val calendar = Calendar.getInstance() // Bieżąca data
        calendar.add(Calendar.DAY_OF_YEAR, 7) //margines tygodnia
        val currentDate = calendar.time
        notificationDao.deleteOldNotifications(expiryDate = currentDate)

    }
}
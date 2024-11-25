package com.example.medicin_app_v2.workers

import android.content.Context
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.medicin_app_v2.data.AppDatabase
import com.example.medicin_app_v2.data.usage.Usage
import kotlinx.coroutines.flow.first
import java.util.Calendar

private const val TAG = "UsageWorker"
class DeleteUsageWorker(
    ctx: Context,
    params: WorkerParameters
): CoroutineWorker(ctx, params) {

    private val usageDao  = AppDatabase.getDatabase(ctx).usageDao()
    private val notificationDao  = AppDatabase.getDatabase(ctx).notificationDao()


    override suspend fun doWork(): Result {

        return try{
            deleteArchivals()
            deleteNotifications()
            Result.success()
        }catch (e: Exception) {
            Log.e(TAG, "failed due to :", e)
            Result.failure()
        }
    }


    fun deleteArchivals()
    {
        val calendar = Calendar.getInstance() // Bieżąca data
        calendar.add(Calendar.MINUTE, 10) //danie marginesu 10 minut
        val currentDate = calendar.time

        usageDao.deleteOldEvents(expiryDate = currentDate)
    }

    fun deleteNotifications()
    {
        val calendar = Calendar.getInstance() // Bieżąca data
        calendar.add(Calendar.HOUR, 6) //danie marginesu 10 minut
        val currentDate = calendar.time
        notificationDao.deleteOldNotifications(expiryDate = currentDate)

    }




}
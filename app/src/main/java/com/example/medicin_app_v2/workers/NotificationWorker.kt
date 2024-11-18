package com.example.medicin_app_v2.workers

import android.content.Context
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.medicin_app_v2.data.AppDatabase
import com.example.medicin_app_v2.schedulers.AlarmItem
import com.example.medicin_app_v2.schedulers.AlarmScheduler
import com.example.medicin_app_v2.schedulers.AlarmSchedulerImpl
import java.util.Calendar
import java.util.Date

class NotificationWorker(ctx: Context,
    params: WorkerParameters
): CoroutineWorker(ctx, params) {

    private val notificationDao = AppDatabase.getDatabase(ctx).notificationDao()
    private val alarmScheduler: AlarmScheduler = AlarmSchedulerImpl(ctx)
    override suspend fun doWork(): Result {
        Log.i("Alarm", "in do work")

        val calendar = Calendar.getInstance().apply {
            time = Date()
            set(Calendar.HOUR_OF_DAY, 21)
            set(Calendar.MINUTE, 30)
            set(Calendar.SECOND, 0)
        }
        val alarmTime = calendar.time

        // Create an AlarmItem
        val alarmItem = AlarmItem(
            alarmTime = alarmTime,
            message = "Time to take your medication"
        )
        Log.i("Alarm", "$alarmItem")

        // Schedule the alarm using AlarmScheduler
        alarmScheduler.schedule(alarmItem)

        return Result.success()
    }



}

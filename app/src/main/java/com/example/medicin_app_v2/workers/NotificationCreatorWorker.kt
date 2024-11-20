package com.example.medicin_app_v2.workers

import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import android.content.Context
import android.util.Log
import com.example.medicin_app_v2.data.AppDatabase
import com.example.medicin_app_v2.data.USAGE_DETAILS_LIST_KEY
import com.example.medicin_app_v2.data.notification.Notification
import com.example.medicin_app_v2.ui.home.UsageDetails
import com.google.gson.Gson
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import java.util.Calendar

private const val TAG = "NotificationCreatorWorker"

class NotificationCreatorWorker(appContext: Context, workerParams: WorkerParameters) : CoroutineWorker(appContext, workerParams) {

    private val notificationDao = AppDatabase.getDatabase(appContext).notificationDao()
   // private val usageDao  = AppDatabase.getDatabase(appContext).usageDao()


    override suspend fun doWork(): Result {
        Log.i(TAG, "w doWork")
        val usageDetailsList = inputData.getStringArray(USAGE_DETAILS_LIST_KEY)
        Log.i(TAG, "po pobraniu")
        if(usageDetailsList!=null) {
            Log.i(TAG, "nie jest nullem")
            val list = usageDetailsList.toList()
            val gson = Gson()
            //val usages = list.map { Json.decodeFromString<UsageDetails>(it) }
            val usages = list.map { gson.fromJson(it, UsageDetails::class.java) }
            Log.i(TAG, "$usages")
            createNotificationsFromUsage(usages)
            return Result.success()
        }
        else
        {
            Log.i(TAG, "Puste data")
            return Result.failure()
        }
    }

    suspend fun createNotificationsFromUsage(usages: List<UsageDetails>)
    {
        for(usage in usages)
        {
            if(!usage.confirmed)
            {
                Log.i(TAG, "użycie niepotwierdzone")
                createNotification(usage)
            }

        }
    }

    suspend fun createNotification(usage: UsageDetails)
    {
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)
        val dayNow = calendar.get(Calendar.DAY_OF_YEAR)
        calendar.time = usage.date
        val dayUsage = calendar.get(Calendar.DAY_OF_YEAR)

        if(dayUsage == dayNow) {
            Log.i(TAG, "ten sam dzień")
            val notification = Notification(
                Usageid = usage.id,
                title = "Nadchodzące zażycie leku ${usage.medicinDetails.name}",
                info = "${if(usage.patientsName.isBlank()) "Pacjent" else usage.patientsName} powinien/powinna zażyć ${usage.dose} x ${usage.medicinDetails.form} leku ${usage.medicinDetails.name}" +
                        "UWAGA: relacja z posiłkiem: ${usage.medicinDetails.relation}",
                seen = false,
                date = calendar.time
            )

            if (shouldCreateNew(notification)) {
                Log.i (TAG, "insert, usegiId = ${usage.id} , a w nots: ${notification.Usageid}")
                notificationDao.insert(notification)
                Log.i(TAG, "stworzono notification $notification")
            }
        }
    }

    suspend fun shouldCreateNew(notification: Notification) : Boolean
    {
        if(notification.Usageid!=null) {
            val notificationsAboutUsage =
                notificationDao.getAllnotificationABoutUsage(notification.Usageid).filterNotNull()
                    .first()
            Log.i(TAG, "pobrano listę")
            if (notificationsAboutUsage.isEmpty()) {
                Log.i(TAG, "Lista pusta")
                return true
            }
            if (!notificationsAboutUsage[0].seen) {
                Log.i(TAG, "nie widziano ostatniego potwierdzenia")
                val calendar = Calendar.getInstance()
                calendar.time = notificationsAboutUsage[0].date
                //ni epowinno byc stryhardowane
                calendar.add(Calendar.MINUTE, 3)
                if (notification.date >= calendar.time) {
                    Log.i(TAG, "mineyły 3 minuty od ostatniego powiadomienia")
                    return true
                }
            }
            return false
        }
        return true
    }




}
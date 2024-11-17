package com.example.medicin_app_v2.workers

import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import android.content.Context
import android.util.Log
import androidx.work.Data
import com.example.medicin_app_v2.data.AppDatabase
import com.example.medicin_app_v2.data.USAGE_DETAILS_LIST_KEY
import com.example.medicin_app_v2.data.usage.Usage
import com.example.medicin_app_v2.ui.home.UsageDetails
import com.google.gson.Gson
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.serialization.json.Json
import java.util.Calendar

private const val TAG = "NotificationWorker"

class NotificationWorker(appContext: Context, workerParams: WorkerParameters) : CoroutineWorker(appContext, workerParams) {

    private val notificationDao = AppDatabase.getDatabase(appContext).notificationDao()
    private val usageDao  = AppDatabase.getDatabase(appContext).usageDao()


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
        }
        else
        {
            Log.i(TAG, "Puste data")
            return Result.failure()
        }
        TODO("Not yet implemented")
    }

    suspend fun createNotificationsFromUsage(usages: List<UsageDetails>)
    {
       // val usages = getCurrentUsages()
        for(usage in usages)
        {


        }

    }

    suspend fun getCurrentUsages(): List<Usage>
    {
        val calendar = Calendar.getInstance() // Bieżąca data
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)
        val currentDate = calendar.time
        calendar.add(Calendar.DAY_OF_YEAR,1)
        val tomorrow = calendar.time

        val usages = usageDao.getUsagesByDate(nextDay = tomorrow, today = currentDate).filterNotNull().first()
        return usages
    }





}
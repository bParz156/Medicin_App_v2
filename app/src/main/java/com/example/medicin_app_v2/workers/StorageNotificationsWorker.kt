package com.example.medicin_app_v2.workers

import android.content.Context
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.medicin_app_v2.data.AppDatabase
import com.example.medicin_app_v2.data.STORAGE_DETAILS_LIST_KEY
import com.example.medicin_app_v2.data.notification.Notification
import com.example.medicin_app_v2.ui.home.UsageDetails
import com.example.medicin_app_v2.ui.magazyn.StorageDetails
import com.google.gson.Gson
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import java.util.Calendar

private const val TAG = "StoaregCreatorWorker"

class StorageNotificationsWorker(
    ctx: Context,
    params: WorkerParameters
): CoroutineWorker(ctx, params) {

    private val notificationDao = AppDatabase.getDatabase(ctx).notificationDao()

    override suspend fun doWork(): Result {
        val storageDetailsList = inputData.getStringArray(STORAGE_DETAILS_LIST_KEY)
        if(storageDetailsList!=null) {
            Log.i(TAG, "nie jest nullem")
            val list = storageDetailsList.toList()
            val gson = Gson()
            //val usages = list.map { Json.decodeFromString<UsageDetails>(it) }
            val usages = list.map { gson.fromJson(it, StorageDetails::class.java) }
            Log.i(TAG, "$usages")
            createNotifications(usages)
            return Result.success()
        }
        else
        {
            Log.i(TAG, "Puste data")
            return Result.failure()
        }
    }

    suspend fun createNotifications(storages: List<StorageDetails>)
    {
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)
        val today = calendar.time
        for(storage in storages)
        {
            val notification = Notification(
                Usageid = null,
                title = "Zapasy leku się kończą",
                info = "Zapasy leku ${storage.medName} skończą się za ${storage.daysToEnd}",
                seen = false,
                date = today
            )
           // if (shouldCreateNew(notification)) {
            //TODO nie powinno tworzyć za każdym razem - tylko gdy minęło min 5 minut od poprezdniuego wejścia
            notificationDao.insert(notification)
          //      Log.i(TAG, "stworzono notification $notification")
         //   }
        }
    }


}
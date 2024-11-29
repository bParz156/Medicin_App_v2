package com.example.medicin_app_v2.data

import android.content.Context
import android.content.Intent
import android.os.PowerManager
import android.provider.Settings
import android.util.Log
import androidx.core.content.ContextCompat.startActivity
import androidx.work.Data
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.OutOfQuotaPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.example.medicin_app_v2.ui.home.UsageDetails
import com.example.medicin_app_v2.ui.magazyn.StorageDetails
import com.example.medicin_app_v2.workers.DeleteUsageWorker
import com.example.medicin_app_v2.workers.NotificationCreatorWorker
import com.example.medicin_app_v2.workers.NotificationWorker
import com.example.medicin_app_v2.workers.StorageNotificationsWorker
import com.example.medicin_app_v2.workers.UsageWorker
import java.util.concurrent.TimeUnit
import com.google.gson.Gson
import androidx.work.*
import com.example.medicin_app_v2.workers.DeleteNotificationWorker
import com.example.medicin_app_v2.workers.DeleteStorageWorker

//private const val TAG = "UsageWorker"
private const val TAG = "NotificationWorker"
const val USAGE_DETAILS_LIST_KEY = "usageDetailsList"
const val STORAGE_DETAILS_LIST_KEY = "storageDetailsList"
class WorkManagerRepository(context: Context,
    ) : WorkerRepository {

    private val workManager = WorkManager.getInstance(context)
    private val context = context
    //override val outputWorkInfo: Flow<WorkInfo?> = MutableStateFlow(null)

    override fun generateUsages() {
        Log.i(TAG, "in repositori")
       val usageBuilder = PeriodicWorkRequestBuilder<UsageWorker>(1, TimeUnit.HOURS).build()
       val usageBuilderOnce = OneTimeWorkRequestBuilder<UsageWorker>()

     workManager.enqueue(usageBuilderOnce.build())
        workManager.enqueueUniquePeriodicWork(
            "UsageWorker",
            ExistingPeriodicWorkPolicy.KEEP,
            usageBuilder
        )
    }

    override fun deleteAncient() {
        val deleteBuilder = OneTimeWorkRequestBuilder<DeleteUsageWorker>()
        workManager.enqueue(deleteBuilder.build())

        val deleteBuilderPeriodic = PeriodicWorkRequestBuilder<DeleteUsageWorker>(1, TimeUnit.DAYS).build()
        workManager.enqueueUniquePeriodicWork(
            "DeleteUsageWorker",
            ExistingPeriodicWorkPolicy.KEEP,
            deleteBuilderPeriodic
        )

    }

    override fun createNotificationsFromUsages(usageDetailsList: List<UsageDetails>) {
        Log.i(TAG, "generateNotifications")
        val notificationBuilder = OneTimeWorkRequestBuilder<NotificationCreatorWorker>()
        Log.i(TAG, "przed setInpit")
        notificationBuilder.setInputData(createInputDataForNotificationWorker(usageDetailsList))
        Log.i(TAG, "po setInpit")
        workManager.enqueue(notificationBuilder.build())
    }

//    override fun generateNotifications() {
//        val notificationAlarmBuilder = OneTimeWorkRequestBuilder<NotificationWorker>()
//        workManager.enqueue(notificationAlarmBuilder.build())
//
//    }

    override fun notificationStorage() {
        Log.i("StoaregCreatorWorker", "w workManagerRepositorii")

        val powerManager = context.getSystemService(Context.POWER_SERVICE) as PowerManager
        val isIgnoring = powerManager.isIgnoringBatteryOptimizations(context.packageName)

        if (!isIgnoring) {
            // Poproś o wyłączenie optymalizacji
            val intent = Intent(Settings.ACTION_IGNORE_BATTERY_OPTIMIZATION_SETTINGS)
            startActivity(context, intent, null)
        }
        val constraints = Constraints.Builder()
            .setRequiresBatteryNotLow(false)  // Wymaga, aby urządzenie miało wystarczający poziom baterii
            .setRequiresDeviceIdle(false)    // Może działać, gdy urządzenie jest aktywne
            .setRequiredNetworkType(NetworkType.NOT_REQUIRED)
            .setRequiresCharging(false)
            .build()
        val notificationStorageBuilder = PeriodicWorkRequestBuilder<StorageNotificationsWorker>(
            3, TimeUnit.HOURS)
            .setConstraints(constraints)
            .build()
        //workManager.
        workManager.enqueueUniquePeriodicWork(
            "NotificationStorageWorker",
            ExistingPeriodicWorkPolicy.UPDATE,
            notificationStorageBuilder
        )

    }

    override fun deleteNotifications() {
        val notificationDeleteBuilder = PeriodicWorkRequestBuilder<DeleteNotificationWorker>(
            1, TimeUnit.DAYS)
            .build()
        //workManager.
        workManager.enqueueUniquePeriodicWork(
            "DeleteNotificationWorker",
            ExistingPeriodicWorkPolicy.KEEP,
            notificationDeleteBuilder
        )
    }

    override fun deleteStorages() {
        val storageDeleteBuilder = PeriodicWorkRequestBuilder<DeleteStorageWorker>(
            1, TimeUnit.DAYS
        ).build()

        workManager.enqueueUniquePeriodicWork(
            "DeleteStorageWorker",
            ExistingPeriodicWorkPolicy.KEEP,
            storageDeleteBuilder
        )
    }


    private fun createInputDataForNotificationWorker(usageDetailsList: List<UsageDetails>) : Data {
        val builder = Data.Builder()
        val gson = Gson()
        val jsonList = usageDetailsList.map { gson.toJson(it) }
        builder.putStringArray(USAGE_DETAILS_LIST_KEY, jsonList.toTypedArray())
        return builder.build()
    }
    private fun createInputDataForWorker(storageDetailsList: List<StorageDetails>) : Data {
        val builder = Data.Builder()
        val gson = Gson()
        val jsonList = storageDetailsList.map { gson.toJson(it) }
        builder.putStringArray(STORAGE_DETAILS_LIST_KEY, jsonList.toTypedArray())
        return builder.build()
    }
}


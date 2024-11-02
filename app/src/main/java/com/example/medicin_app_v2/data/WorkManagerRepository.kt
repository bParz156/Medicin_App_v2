package com.example.medicin_app_v2.data

import android.content.Context
import android.util.Log
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkInfo
import androidx.work.WorkManager
import com.example.medicin_app_v2.workers.DeleteUsageWorker
import com.example.medicin_app_v2.workers.UsageWorker
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import java.util.concurrent.TimeUnit

private const val TAG = "UsageWorker"
class WorkManagerRepository(context: Context,
    ) : WorkerRepository {

    private val workManager = WorkManager.getInstance(context)
    //override val outputWorkInfo: Flow<WorkInfo?> = MutableStateFlow(null)

    override fun generateUsages() {
        Log.i(TAG, "in repositori")
       val usageBuilder = PeriodicWorkRequestBuilder<UsageWorker>(2, TimeUnit.MINUTES).build()
     //  val usageBuilder = OneTimeWorkRequestBuilder<UsageWorker>()

//     workManager.enqueue(usageBuilder.build())
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
}
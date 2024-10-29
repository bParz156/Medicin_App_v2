package com.example.medicin_app_v2.data

import android.content.Context
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.example.medicin_app_v2.workers.UsageWorker

class WorkManagerRepository(context: Context) : WorkerRepository {

    private val workManager = WorkManager.getInstance(context)

    override fun generateUsages() {
       val usageBuilder = OneTimeWorkRequestBuilder<UsageWorker>()

     workManager.enqueue(usageBuilder.build())
    }
}
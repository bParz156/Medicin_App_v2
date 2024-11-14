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

    override suspend fun doWork(): Result {
        val allUsages = usageDao.getAllUsages().first()

        return try{
            deleteArchivals(allUsages)
            Result.success()
        }catch (e: Exception) {
            Log.e(TAG, "failed due to :", e)
            Result.failure()
        }
    }


    suspend fun deleteArchivals(usageList: List<Usage>)
    {
        val calendar = Calendar.getInstance() // Bieżąca data
        val currentDate = calendar.time
        for(usage in usageList)
        {
            if(usage.confirmed || usage.date < currentDate)
            {
                usageDao.delete(usage)
            }
        }
    }

//    suspend fun deleteAll(usageList: List<Usage>)
//    {
//        for(usage in usageList)
//        {
//                usageDao.delete(usage)
//        }
//    }



}
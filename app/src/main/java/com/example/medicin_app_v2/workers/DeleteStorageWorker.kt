package com.example.medicin_app_v2.workers

import android.content.Context
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.medicin_app_v2.data.AppDatabase
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import java.util.Calendar

class DeleteStorageWorker(
    ctx: Context,
    params: WorkerParameters
): CoroutineWorker(ctx, params) {

    private val scheduleDao = AppDatabase.getDatabase(ctx).scheduleDao()
    private val storageDao = AppDatabase.getDatabase(ctx).storageDao()

    override suspend fun doWork(): Result {

        return try{
            deleteUnused()
            Result.success()
        }catch (e: Exception) {
            Result.failure()
        }
    }


    suspend fun deleteUnused()
    {
        val allStorages = storageDao.getAllStorages().filterNotNull().first()
        val today = Calendar.getInstance().time
        for(storage in allStorages)
        {
            val medicinSchedule = scheduleDao.getActiveSchedulesByMedicine(today, storage.Medicineid).filterNotNull().first()
            if(medicinSchedule.isEmpty())
            {
                storageDao.delete(storage)
            }
        }


    }
}
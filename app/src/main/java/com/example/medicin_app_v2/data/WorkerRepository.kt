package com.example.medicin_app_v2.data

import androidx.work.WorkInfo
import com.example.medicin_app_v2.ui.home.UsageDetails
import kotlinx.coroutines.flow.Flow

interface WorkerRepository {
   // val outputWorkInfo: Flow<WorkInfo?>
    fun generateUsages()
    fun deleteAncient()
    fun generateNotifications(usageDetailsList: List<UsageDetails>)
}
package com.example.medicin_app_v2.data

import com.example.medicin_app_v2.ui.home.UsageDetails
import com.example.medicin_app_v2.ui.magazyn.StorageDetails

interface WorkerRepository {
   // val outputWorkInfo: Flow<WorkInfo?>
    fun generateUsages()
    fun deleteAncient()
    fun createNotificationsFromUsages(usageDetailsList: List<UsageDetails>)
    fun generateNotifications()
    fun notificationsAboutStorage(storageDetailsList: List<StorageDetails>)
}
package com.example.medicin_app_v2.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import com.example.medicin_app_v2.data.firstAidKit.FirstaidkitRepository
import com.example.medicin_app_v2.data.firstAidKit.OfflineFirstaidkitRepository
import com.example.medicin_app_v2.data.medicine.MedicinRepository
import com.example.medicin_app_v2.data.medicine.OfflineMedicinRepository
import com.example.medicin_app_v2.data.notification.NotificationRepository
import com.example.medicin_app_v2.data.notification.OfflineNotificationRepository
import com.example.medicin_app_v2.data.patient.OfflinePatientsRepository
import com.example.medicin_app_v2.data.patient.PatientsRepository
import com.example.medicin_app_v2.data.schedule.OfflineScheduleRepository
import com.example.medicin_app_v2.data.schedule.ScheduleRepository
import com.example.medicin_app_v2.data.scheduleTerms.OfflineScheduleTermRepository
import com.example.medicin_app_v2.data.scheduleTerms.ScheduleTermRepository
import com.example.medicin_app_v2.data.storage.OfflineStorageRepository
import com.example.medicin_app_v2.data.storage.StorageRepository
import com.example.medicin_app_v2.data.usage.OfflineUsageRepository
import com.example.medicin_app_v2.data.usage.UsageRepository
import com.example.medicin_app_v2.dataStore


interface AppContainer {
    val patientsRepository: PatientsRepository
    val firstaidkitRepository : FirstaidkitRepository
    val medicinRepository : MedicinRepository
    val notificationRepository : NotificationRepository
    val scheduleRepository : ScheduleRepository
    val storageRepository : StorageRepository
    val usageRepository : UsageRepository
    val scheduleTermRepository: ScheduleTermRepository
    val userPreferencesRepository: UserPreferencesRepository
    val workerRepository : WorkerRepository
}


class AppDataContainer(private val context: Context) : AppContainer {

    override val patientsRepository: PatientsRepository by lazy {
        OfflinePatientsRepository(AppDatabase.getDatabase(context).patientDao())
    }

    override val firstaidkitRepository: FirstaidkitRepository by lazy {
        OfflineFirstaidkitRepository(AppDatabase.getDatabase(context).firstAidKitDao())
    }

    override val medicinRepository: MedicinRepository by lazy {
        OfflineMedicinRepository(AppDatabase.getDatabase(context).medicineDao())
    }

    override val notificationRepository: NotificationRepository by lazy {
        OfflineNotificationRepository(AppDatabase.getDatabase(context).notificationDao())
    }

    override val scheduleRepository: ScheduleRepository by lazy {
        OfflineScheduleRepository(AppDatabase.getDatabase(context).scheduleDao())
    }

    override val storageRepository: StorageRepository by lazy {
        OfflineStorageRepository(AppDatabase.getDatabase(context).storageDao())
    }


    override val usageRepository: UsageRepository by lazy {
        OfflineUsageRepository(AppDatabase.getDatabase(context).usageDao())
    }
    override val scheduleTermRepository: ScheduleTermRepository by lazy {
        OfflineScheduleTermRepository(AppDatabase.getDatabase(context).scheduleTermDao())
     }

    override val userPreferencesRepository: UserPreferencesRepository by lazy {
        UserPreferencesRepository(context.dataStore)
    }
    override val workerRepository: WorkerRepository by lazy {
        WorkManagerRepository(context)
    }


}
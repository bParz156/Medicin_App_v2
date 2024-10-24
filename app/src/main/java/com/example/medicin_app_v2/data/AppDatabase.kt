package com.example.medicin_app_v2.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.medicin_app_v2.data.scheduleTerms.ScheduleTerm
import com.example.medicin_app_v2.data.firstAidKit.FirstAidKit
import com.example.medicin_app_v2.data.firstAidKit.FirstAidKitDao
import com.example.medicin_app_v2.data.medicine.Medicine
import com.example.medicin_app_v2.data.medicine.MedicineDao
import com.example.medicin_app_v2.data.notification.Notification
import com.example.medicin_app_v2.data.notification.NotificationDao
import com.example.medicin_app_v2.data.patient.Patient
import com.example.medicin_app_v2.data.patient.PatientDao
import com.example.medicin_app_v2.data.schedule.Schedule
import com.example.medicin_app_v2.data.schedule.ScheduleDao
import com.example.medicin_app_v2.data.scheduleTerms.ScheduleTermDao
import com.example.medicin_app_v2.data.storage.Storage
import com.example.medicin_app_v2.data.storage.StorageDao
import com.example.medicin_app_v2.data.usage.Usage
import com.example.medicin_app_v2.data.usage.UsageDao

@Database(entities= [Patient::class, Medicine::class, Storage::class, FirstAidKit::class, Schedule::class, Usage::class, Notification::class , ScheduleTerm::class],
    version=4, exportSchema = false)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {

    abstract fun patientDao(): PatientDao
    abstract fun medicineDao(): MedicineDao
    abstract fun storageDao(): StorageDao
    abstract fun firstAidKitDao(): FirstAidKitDao
    abstract fun scheduleDao(): ScheduleDao
    abstract fun usageDao(): UsageDao
    abstract fun notificationDao(): NotificationDao
    abstract fun scheduleTermDao(): ScheduleTermDao
    companion object
    {
        @Volatile
        private var Instance : AppDatabase? = null

        fun getDatabase(context: Context) : AppDatabase
        {
            return Instance?: synchronized(this)
            {
                Room.databaseBuilder(context, AppDatabase::class.java, "medicin_database")
                    .fallbackToDestructiveMigration()
                    .build()
                    .also{ Instance = it}
            }
        }
    }
}
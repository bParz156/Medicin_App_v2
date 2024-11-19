package com.example.medicin_app_v2.workers

import android.Manifest
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.medicin_app_v2.R
import com.example.medicin_app_v2.data.AlertDetails
import com.example.medicin_app_v2.data.AppDatabase
import com.example.medicin_app_v2.data.DayWeek
import com.example.medicin_app_v2.data.STORAGE_DETAILS_LIST_KEY
import com.example.medicin_app_v2.data.notification.Notification
import com.example.medicin_app_v2.ui.home.ScheduleTermDetails
import com.example.medicin_app_v2.ui.home.UsageDetails
import com.example.medicin_app_v2.ui.home.toMedicinDetails
import com.example.medicin_app_v2.ui.home.toScheduleTermDetails
import com.example.medicin_app_v2.ui.magazyn.StorageDetails
import com.example.medicin_app_v2.ui.zalecenia.toStorageDetails
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
    private val storageDao = AppDatabase.getDatabase(ctx).storageDao()
    private val medicineDao = AppDatabase.getDatabase(ctx).medicineDao()
    private val scheduleTermDao = AppDatabase.getDatabase(ctx).scheduleTermDao()
    private val scheduleDao = AppDatabase.getDatabase(ctx).scheduleDao()
    private val firstAidKitDao = AppDatabase.getDatabase(ctx).firstAidKitDao()
    private val patientDao = AppDatabase.getDatabase(ctx).patientDao()
    private val context = ctx

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
            val list = getStorageList()
            createNotifications(list)
            Log.i(TAG, "Puste data")
            return Result.success()
            //return Result.failure()
        }
    }


    suspend fun getStorageList(): List<StorageDetails>
    {
        val storages = storageDao.getAllStorages().filterNotNull().first()

        val list : MutableList<StorageDetails> = mutableListOf()

        for (storage in storages)
        {
            val medicinDetails = medicineDao.getmedicine(storage.Medicineid).first().toMedicinDetails()

            val patientsSharing = firstAidKitDao.getfirstAidKitByStorage(storage.id).first().map { it.Patient_id }

            val schedules = patientsSharing.map { scheduleDao.getPatientMedcicineSchedule(patient_id = it , medicine_id = medicinDetails.id).filterNotNull().first() }

            val scheduleTermDetailsList : MutableList<ScheduleTermDetails> = mutableListOf()

            for(schedule in schedules)
            {
                Log.i(TAG, "schedule: $schedule")
                scheduleTermDetailsList+=
                scheduleTermDao.getScheduleTermBySchedule(
                    scheduleId = schedule.id
                ).filterNotNull().first().map { it.toScheduleTermDetails() }

            }

            val storageDetails = StorageDetails(
                storageId = storage.id,
                medicinId = storage.Medicineid,
                quantity = storage.quantity,
                medName = medicinDetails.name,
                medicinForm =  medicinDetails.form,
                daysToEnd =   calculateDaysToEnd(
                    scheduleTermDetailsList = scheduleTermDetailsList,
                    quantity = storage.quantity
                )
            )
            Log.i(TAG, "dni do końca leku ${medicinDetails.name} == ${storageDetails.daysToEnd}")
            if(storageDetails.daysToEnd<7)
            {
                list +=storageDetails
            }

        }
        return  list
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
                info = "Zapasy leku ${storage.medName} skończą się za ${storage.daysToEnd} dni",
                seen = false,
                date = today
            )
           // if (shouldCreateNew(notification)) {
            //TODO nie powinno tworzyć za każdym razem - tylko gdy minęło min 5 minut od poprezdniuego wejścia
           val id = notificationDao.insert(notification)
            val CHANNEL_ID = "alarm_id"

            val builder = NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_launcher_background)
                .setContentTitle(notification.title)
                .setContentText(notification.info)
//                .setStyle(
//                    NotificationCompat.BigTextStyle()
//                    .bigText("Much longer text that cannot fit one line..."))
                .setPriority(NotificationCompat.PRIORITY_MAX)
                .setWhen(notification.date.time)
          //      Log.i(TAG, "stworzono notification $notification")
         //   }
            val intent = Intent(context, AlertDetails::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                putExtra("notification_id", id)
            }
            val pendingIntent: PendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT)
            builder.setContentIntent(pendingIntent)
            builder.setAutoCancel(true)

            with(NotificationManagerCompat.from(context)) {
                if (ActivityCompat.checkSelfPermission(
                        context,
                        Manifest.permission.POST_NOTIFICATIONS
                    ) != PackageManager.PERMISSION_GRANTED
                ) {

                    // TODO: Consider calling
                    // ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    // public fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>,
                    //                                        grantResults: IntArray)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.

                    return@with
                }
                notify(id.toInt(), builder.build())
            }
        }
    }



    private fun calculateDaysToEnd(scheduleTermDetailsList: List<ScheduleTermDetails>, quantity: Int) : Int
    {
        //DO POPRAWYYY
        val calendar = Calendar.getInstance()
        var dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK)
        var toEnd =0
        var stopPit: Boolean = false
        if(scheduleTermDetailsList.isNotEmpty()) {
            val usageDay: List<Int> = calculateWeeklyUsage(scheduleTermDetailsList)
            // Log.i("calday", "${DayWeek.values().find{it.weekDay==1}?.name}  ${usageDay[0]}")
            // Log.i("calday", "${DayWeek.values().find{it.weekDay==5}?.name}  ${usageDay[4]}")
            var used = 0
            while (used <= quantity && !stopPit) {
                Log.i("calday", "while: used = ${used} , toEnd= ${toEnd}  dayOfWeek =${dayOfWeek}")
                if (usageDay[dayOfWeek] + used > quantity)
                    stopPit = true
                else {
                    used += usageDay[dayOfWeek]
                    dayOfWeek = (dayOfWeek + 1) % 7
                    toEnd++
                }

            }
            Log.i("calday", "nie wychodzi z while")
        }
        return toEnd
    }

    private fun calculateWeeklyUsage(scheduleTermDetailsList: List<ScheduleTermDetails>): List<Int>
    {
        val list = MutableList(7) { 0 }
        for(day in DayWeek.values())
        {
            list[day.weekDay-1] = calculateDayUsage(scheduleTermDetailsList, day)
            Log.i("calday", "${day.name}  -- ${list[day.weekDay -1]}")
        }
        return list
    }

    private fun calculateDayUsage(scheduleTermDetailsList: List<ScheduleTermDetails>, dayWeek: DayWeek): Int
    {
        var uasage: Int =0
        for ( scheduleTermDetail in scheduleTermDetailsList)
        {
            if(scheduleTermDetail.day == dayWeek)
            {
                uasage+=scheduleTermDetail.dose
            }

        }

        return  uasage
    }


}
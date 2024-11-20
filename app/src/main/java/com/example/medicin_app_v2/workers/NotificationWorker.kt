package com.example.medicin_app_v2.workers

import android.Manifest
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.medicin_app_v2.R
import com.example.medicin_app_v2.data.AlertDetails
import com.example.medicin_app_v2.data.AppDatabase
import com.example.medicin_app_v2.data.notification.Notification
import com.example.medicin_app_v2.schedulers.AlarmItem
import com.example.medicin_app_v2.schedulers.AlarmScheduler
import com.example.medicin_app_v2.schedulers.AlarmSchedulerImpl
import com.example.medicin_app_v2.ui.home.UsageDetails
import com.google.gson.Gson
import java.util.Calendar
import java.util.Date

class NotificationWorker(ctx: Context,
    params: WorkerParameters
): CoroutineWorker(ctx, params) {

    private val notificationDao = AppDatabase.getDatabase(ctx).notificationDao()
    private val usageDao  = AppDatabase.getDatabase(ctx).usageDao()
    private val scheduleDao = AppDatabase.getDatabase(ctx).scheduleDao()
    val medicinDao= AppDatabase.getDatabase(ctx).medicineDao()
    val scheduleTermDao = AppDatabase.getDatabase(ctx).scheduleTermDao()
    private val patientsDao = AppDatabase.getDatabase(ctx).patientDao()
    private val alarmScheduler: AlarmScheduler = AlarmSchedulerImpl(ctx)
    private val context = ctx

    override suspend fun doWork(): Result {

        val inputData = inputData.getString("usage")
        val gson = Gson()
        val usageDetails = gson.fromJson(inputData, UsageDetails::class.java)
        createNotification(usageDetails)


        return Result.success()
    }

    suspend fun createNotification(usage: UsageDetails) {
       // Log.i(TAG, "ten sam dzień")
        val calendar = Calendar.getInstance()
        calendar.time = usage.date
        val hour = calendar.get(Calendar.HOUR_OF_DAY)
        val minute = calendar.get(Calendar.MINUTE)
        val notification = Notification(
            Usageid = usage.id,
            title = "Nadchodzące zażycie leku ${usage.medicinDetails.name}",
            info = "Godzina: ${"%02d".format(hour)}:${"%02d".format(minute)} ${usage.patientsName} powinien/powinna zażyć ${usage.dose} x ${usage.medicinDetails.form} leku ${usage.medicinDetails.name}" +
                    "UWAGA: relacja z posiłkiem: ${usage.medicinDetails.relation}",
            seen = false,
            date = usage.date
        )

        val id = notificationDao.insert(notification)

        val CHANNEL_ID = "alarm_id"

        val builder = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_launcher_background)
            .setContentTitle(notification.title)
            .setContentText(notification.info)
            .setStyle(
                NotificationCompat.BigTextStyle()
                    .bigText(notification.info)
            )
            .setPriority(NotificationCompat.PRIORITY_MAX)
            .setWhen(notification.date.time)
            .setColor(Color.GREEN)
            .setColorized(true)
            .setCategory(NotificationCompat.CATEGORY_EVENT)

        //   }
        val intent = Intent(context, AlertDetails::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            putExtra("notification_id", id)
        }
        val pendingIntent: PendingIntent = PendingIntent.getActivity(
            context,
            0,
            intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )
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
        //    Log.i(TAG, "po notify")

        }
    }


}

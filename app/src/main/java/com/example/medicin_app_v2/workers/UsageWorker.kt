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
import androidx.work.Data
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import com.example.medicin_app_v2.R
import com.example.medicin_app_v2.data.AlertDetails
import com.example.medicin_app_v2.data.AppDatabase
import com.example.medicin_app_v2.data.USAGE_DETAILS_LIST_KEY
import com.example.medicin_app_v2.data.medicine.MedicinRepository
import com.example.medicin_app_v2.data.notification.Notification
import com.example.medicin_app_v2.data.schedule.ScheduleRepository
import com.example.medicin_app_v2.data.scheduleTerms.ScheduleTermRepository
import com.example.medicin_app_v2.data.usage.Usage
import com.example.medicin_app_v2.data.usage.UsageRepository
import com.example.medicin_app_v2.ui.home.PatientScheduleDetailsInfo
import com.example.medicin_app_v2.ui.home.PatientScheduleInfo
import com.example.medicin_app_v2.ui.home.ScheduleDetails
import com.example.medicin_app_v2.ui.home.ScheduleTermDetails
import com.example.medicin_app_v2.ui.home.UsageDetails
import com.example.medicin_app_v2.ui.home.toMedicinDetails
import com.example.medicin_app_v2.ui.home.toScheduleDetails
import com.google.gson.Gson
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import java.util.Calendar
import java.util.concurrent.TimeUnit


private const val TAG = "UsageWorker"
class UsageWorker (
ctx: Context,
    params: WorkerParameters,
//    private val usageRepository: UsageRepository,
//    private val scheduleRepository : ScheduleRepository,
//    val medicinRepository: MedicinRepository,
//    val scheduleTermRepository: ScheduleTermRepository
) : CoroutineWorker(ctx, params)  {

    private val usageDao  = AppDatabase.getDatabase(ctx).usageDao()
    private val scheduleDao = AppDatabase.getDatabase(ctx).scheduleDao()
    val medicinDao= AppDatabase.getDatabase(ctx).medicineDao()
    val scheduleTermDao = AppDatabase.getDatabase(ctx).scheduleTermDao()
    private val notificationDao = AppDatabase.getDatabase(ctx).notificationDao()
    private val patientsDao = AppDatabase.getDatabase(ctx).patientDao()
    val context = ctx

    override suspend fun doWork(): Result {
        val allSchedules = scheduleDao.getAllSchedules().first()
            .let { PatientScheduleInfo(it) }// Poczekaj na pierwszy wynik z Flow

        val scheduleDetails = PatientScheduleDetailsInfo(
            allSchedules.scheduleList.map{
                it.toScheduleDetails(
                    medicinDetails =  medicinDao.getmedicine(it.Medicine_id)
//                    medicinDetails = medicinRepository.getMedicineStream(it.Medicine_id)
                        .filterNotNull()
                        .first()
                        .toMedicinDetails(),
                    scheduleTermList =  scheduleTermDao.getScheduleTermBySchedule(it.id)
//                    scheduleTermList = scheduleTermRepository.getAllsSchedulesTerms(it.id)
                        .filterNotNull()
                        .first()
                )
            }
        )

        return try {

            genereteUsagesForNextPeriodOfTime(
                scheduleDetailsList =  scheduleDetails.scheduleDetailsList,
                days =7
            )
            Result.success()
        }catch(e: Exception) {
            Log.e(TAG, "failed due to :", e)
            Result.failure()
        }
    }


    suspend fun genereteUsagesForNextPeriodOfTime(
        scheduleDetailsList: List<ScheduleDetails>,
        days: Int
    ) {
        val calendar = Calendar.getInstance() // Bieżąca data
        val currentDate = calendar.time

            for (scheduleDetail in scheduleDetailsList) {
                val patientName = patientsDao.getpatient(scheduleDetail.patiendId).filterNotNull().first().name
                for (scheduleTerm in scheduleDetail.scheduleTermDetailsList) {
                    for (dayOffset in 0..days) {
                        calendar.time = currentDate
                        calendar.add(Calendar.DAY_OF_YEAR, dayOffset)
                        val eventDate = calendar.time
                        if(eventDate > scheduleDetail.startDate && (scheduleDetail.endDate ==null || eventDate<= scheduleDetail.endDate)) {
                            if (isValidEventDay(scheduleTerm, calendar)) {
                                calendar.set(Calendar.HOUR_OF_DAY, scheduleTerm.hour)
                                calendar.set(Calendar.MINUTE, scheduleTerm.minute)
                                calendar.set(Calendar.SECOND, 0)
                                calendar.set(Calendar.MILLISECOND, 0)
                                val eventTime = calendar.time
                                if(eventTime >= eventDate) {
                                    val usage = Usage(
                                        id = 0,
                                        ScheduleTerm_id = scheduleTerm.id,
                                        confirmed = false,
                                        date = eventTime
                                    )
                                    Log.i(TAG, "bylo?")
                                    val usageFromDB = usageDao.getUsage(
                                        scheduleTerm_id = scheduleTerm.id,
                                        useDate = eventTime
                                    ).first()
                                    if (usageFromDB == null) {
                                        Log.i(TAG, "nie bylo")
                                        val id = usageDao.insert(usage)

                                        val gson = Gson()
                                        val usageDetails=  UsageDetails(
                                            id = id.toInt(),
                                            date = usage.date,
                                            dose = scheduleTerm.dose,
                                            medicinDetails = scheduleDetail.medicinDetails,
                                            confirmed = usage.confirmed,
                                            scheduleTermId = scheduleTerm.id,
                                            storageId = 0,
                                            patientsName = patientName
                                        )
                                        val builder = Data.Builder()
                                        val inputData = gson.toJson(usageDetails, UsageDetails::class.java)
                                        val delay = usage.date.time - System.currentTimeMillis()
                                        builder.putString("usage", inputData)

                                        val nextWorker = OneTimeWorkRequestBuilder<NotificationWorker>()
                                            .setInitialDelay(delay, TimeUnit.MILLISECONDS)
                                            .setInputData(builder.build())
                                            .build()


                                        WorkManager.getInstance(applicationContext)
                                            .enqueue(nextWorker)
//                                        createNotification(usage =  UsageDetails(
//                                            id = id.toInt(),
//                                            date = usage.date,
//                                            dose = scheduleTerm.dose,
//                                            medicinDetails = scheduleDetail.medicinDetails,
//                                            confirmed = usage.confirmed,
//                                            scheduleTermId = scheduleTerm.id,
//                                            storageId = 0,
//                                            patientsName = patientName
//                                        ))
                                    }
                                }
                            }
                        }
                    }
                }

        }
    }

    private fun isValidEventDay(scheduleTerm: ScheduleTermDetails, calendar: Calendar): Boolean {
        val dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK)  // Zwraca dzień tygodnia: 1 = Niedziela, 7 = Sobota
       // Log.i(TAG, "isValidDay  == scheudleDay: $(${scheduleTerm.day.weekDay}) ==? ${dayOfWeek}")
        return scheduleTerm.day.weekDay == dayOfWeek  // Załóżmy, że 'dayOfWeek' w harmonogramie jest zgodne z `Calendar`
    }

    suspend fun createNotification(usage: UsageDetails) {
        Log.i(TAG, "ten sam dzień")
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
            .setStyle(NotificationCompat.BigTextStyle()
                .bigText(notification.info))
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
            Log.i(TAG, "po notify")

        }
    }


}
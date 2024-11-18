package com.example.medicin_app_v2.schedulers

import android.annotation.SuppressLint
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.provider.Settings
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.core.content.getSystemService
import java.time.ZoneId
import java.util.Calendar

class AlarmSchedulerImpl(
    private val context: Context
) : AlarmScheduler {

    val alarmManager = context.getSystemService<AlarmManager>()!!

    //@SuppressLint("ScheduleExactAlarm")
    //@RequiresApi(Build.VERSION_CODES.O)
   // @RequiresApi(Build.VERSION_CODES.S)
    override fun schedule(alarmItem: AlarmItem) {


        //Log.i("Alarm", "Czy może: ${alarmManager.canScheduleExactAlarms()}")

        val intent = Intent(context, AlarmReceiver::class.java).apply {
            putExtra("EXTRA_MESSAGE", alarmItem.message)
        }
        val pendingIntent = PendingIntent.getBroadcast(
            context, alarmItem.hashCode(), intent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        val calendar = Calendar.getInstance()
        calendar.time = alarmItem.alarmTime

//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S && !alarmManager.canScheduleExactAlarms()) {
//            val intent = Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM)
//            context.startActivity(intent)
//        }
        Log.i("Alarm", "alarm na ${calendar.time} ?")

        val alarmClockInfo = AlarmManager.AlarmClockInfo(calendar.timeInMillis, pendingIntent)
//        alarmManager.setAlarmClock(
//            alarmClockInfo, pendingIntent
//        )
        alarmManager.set(
            AlarmManager.RTC_WAKEUP,
            calendar.timeInMillis,
            pendingIntent
        )

//        alarmManager.setExactAndAllowWhileIdle(
//            AlarmManager.RTC_WAKEUP,
//            calendar.timeInMillis,
//            pendingIntent
//        )
        Log.e("Alarm", "Alarm set at ${calendar.time}")
    }

    override fun cancel(alarmItem: AlarmItem) {
        alarmManager.cancel(
            PendingIntent.getBroadcast(
                context,
                alarmItem.hashCode(),
                Intent(context, AlarmReceiver::class.java),
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
        )
    }
}
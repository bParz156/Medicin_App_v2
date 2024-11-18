package com.example.medicin_app_v2.schedulers

import java.util.Date

interface AlarmScheduler {
    fun schedule(alarmItem: AlarmItem)
    fun cancel(alarmItem: AlarmItem)
}

data class AlarmItem(
    val alarmTime : Date,
    val message : String
)
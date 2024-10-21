package com.example.medicin_app_v2.data.scheduleTerms

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import com.example.medicin_app_v2.data.DayWeek
import com.example.medicin_app_v2.data.schedule.Schedule


@Entity(
    tableName = "ScheduleTerm",
    foreignKeys = [
        ForeignKey(
            entity = Schedule::class,
            parentColumns = ["id"],
            childColumns = ["ScheduleId"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)



data class ScheduleTerm(
    @PrimaryKey (autoGenerate = true)
    val id: Int =0,
    val ScheduleId: Int,
    val day: DayWeek,
    val hour: Int,
    val minute: Int,
    val dose: Int
)
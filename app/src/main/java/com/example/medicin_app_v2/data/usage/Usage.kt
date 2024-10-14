package com.example.medicin_app_v2.data.usage

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import com.example.medicin_app_v2.data.schedule.Schedule
import java.util.Date

@Entity(
    tableName = "Usage",
    foreignKeys = [
        ForeignKey(
            entity = Schedule::class,
            parentColumns = ["id"],
            childColumns = ["Schedule_id"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class Usage(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0, // Auto-generowane ID
    val Schedule_id: Int, // Odniesienie do Schedule
    val confirmed: Boolean,
    val date: Date
)

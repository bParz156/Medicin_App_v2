package com.example.medicin_app_v2.data.usage

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import androidx.room.Index
import com.example.medicin_app_v2.data.schedule.Schedule
import com.example.medicin_app_v2.data.scheduleTerms.ScheduleTerm
import java.util.Date

@Entity(
    tableName = "Usage",
    foreignKeys = [
        ForeignKey(
            entity = ScheduleTerm::class,
            parentColumns = ["id"],
            childColumns = ["ScheduleTerm_id"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["ScheduleTerm_id"])]
)
data class Usage(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0, // Auto-generowane ID
    val ScheduleTerm_id: Int, // Odniesienie do Schedule
    val confirmed: Boolean,
    val date: Date
)

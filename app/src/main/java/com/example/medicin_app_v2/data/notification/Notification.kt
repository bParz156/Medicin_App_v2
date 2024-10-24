package com.example.medicin_app_v2.data.notification

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.example.medicin_app_v2.data.usage.Usage
import java.util.Date

@Entity(
    tableName = "Notification",
    foreignKeys = [
        ForeignKey(
            entity = Usage::class,
            parentColumns = ["id"],
            childColumns = ["Usageid"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["Usageid"])]
)
data class Notification(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0, // Auto-generowane ID
    val Usageid: Int, // Odniesienie do Usage
    val title: String,
    val info: String,
    val seen: Boolean,
    val date : Date
)

package com.example.medicin_app_v2.data.storage

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import com.example.medicin_app_v2.data.medicine.Medicine

@Entity(
    tableName = "Storage",
    foreignKeys = [
        ForeignKey(
            entity = Medicine::class,
            parentColumns = ["id"],
            childColumns = ["Medicineid"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class Storage(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0, // Auto-generowane ID
    val Medicineid: Int, // Odniesienie do Medicine
    val quantity: Int
)
package com.example.medicin_app_v2.data.firstAidKit

import androidx.room.Entity
import androidx.room.Index
import androidx.room.ForeignKey
import com.example.medicin_app_v2.data.patient.Patient
import com.example.medicin_app_v2.data.storage.Storage

@Entity(
    tableName = "FirstAidKit",
    primaryKeys = ["Patient_id", "Storage_id"], // Złożony klucz główny
    foreignKeys = [
        ForeignKey(
            entity = Patient::class,
            parentColumns = ["id"],
            childColumns = ["Patient_id"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = Storage::class,
            parentColumns = ["id"],
            childColumns = ["Storage_id"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["Patient_id"]), Index(value = ["Storage_id"])]
)
data class FirstAidKit(
    val Patient_id: Int, // Odniesienie do Patient
    val Storage_id: Int  // Odniesienie do Storage
)

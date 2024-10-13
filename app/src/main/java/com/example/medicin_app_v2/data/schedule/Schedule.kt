package com.example.medicin_app_v2.data.schedule

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import com.example.medicin_app_v2.data.MealRelation
import com.example.medicin_app_v2.data.medicine.Medicine
import com.example.medicin_app_v2.data.patient.Patient
import java.sql.Timestamp

@Entity(
    tableName = "Schedule",
    foreignKeys = [
        ForeignKey(
            entity = Patient::class,
            parentColumns = ["id"],
            childColumns = ["Patient_id"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = Medicine::class,
            parentColumns = ["id"],
            childColumns = ["Medicine_id"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class Schedule(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0, // Auto-generowane ID
    val Patient_id: Int,  // Odniesienie do Patient
    val Medicine_id: Int, // Odniesienie do Medicine
    val day: Int,
    val hour: Float,
    val dose: Int,
    val startDate: Timestamp,
    val endDate: Timestamp?,
    val mealReltion : MealRelation
)

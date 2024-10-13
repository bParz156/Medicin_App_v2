package com.example.medicin_app_v2.data.medicine

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.medicin_app_v2.data.MedicinForm

@Entity(tableName = "medicines")
data class Medicine(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0, // Auto-generowane ID
    val name: String,
    val form: MedicinForm
)

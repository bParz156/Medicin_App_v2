package com.example.medicin_app_v2.data.patient

import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName="patients")
data class Patient(
    @PrimaryKey(autoGenerate= true)
    val id: Int,
    val name: String

)

package com.example.medicin_app_v2.data

import androidx.room.TypeConverter

class Converters {

    @TypeConverter
    fun fromMedicineForm(value: MedicinForm): String {
        return value.name // Zamiana enum na String
    }

    @TypeConverter
    fun toMedicineForm(value: String): MedicinForm {
        return MedicinForm.valueOf(value) // Zamiana String na enum
    }

    @TypeConverter
    fun fromMealRelation(value: MealRelation): String {
        return value.name // Zamiana enum na String
    }

    @TypeConverter
    fun toMealRelation(value: String): MealRelation {
        return MealRelation.valueOf(value) // Zamiana String na enum
    }


}
package com.example.medicin_app_v2.data

import androidx.room.TypeConverter
import com.example.medicin_app_v2.data.examination.ExaminationType
import java.util.Date

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

    @TypeConverter
    fun fromTimestamp(value: Long?): Date? {
        return value?.let { Date(it) }
    }

    @TypeConverter
    fun dateToTimestamp(date: Date?): Long? {
        return date?.time
    }

    @TypeConverter
    fun fromDayWeek(value: DayWeek): String {
        return value.name
    }

    @TypeConverter
    fun toDayWeek(value:String): DayWeek {
        return DayWeek.valueOf(value)
    }

    @TypeConverter
    fun fromExaminationType(value: ExaminationType): String {
        return value.name
    }

    @TypeConverter
    fun toExaminationType(value:String): ExaminationType {
        return ExaminationType.valueOf(value)
    }


}
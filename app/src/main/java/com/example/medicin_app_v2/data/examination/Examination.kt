package com.example.medicin_app_v2.data.examination

import androidx.annotation.StringRes
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.example.medicin_app_v2.R
import com.example.medicin_app_v2.data.patient.Patient
import java.util.Calendar
import java.util.Date


@Entity(
    tableName = "Examination",
    foreignKeys = [
        ForeignKey(
            entity = Patient::class,
            parentColumns = ["id"],
            childColumns = ["Patient_id"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["Patient_id"]), Index(value = ["type"])]
)

data class Examination(
    @PrimaryKey(autoGenerate = true)
    val id: Int =0,
    var Patient_id: Int =0,
    val date: Date = Calendar.getInstance().time,
    val type : ExaminationType = ExaminationType.CUKIER,
    var value : Float = -1f

    )

enum class ExaminationType(@StringRes val title: Int, val dolna : Float, val gorna: Float) {
    CUKIER (title = R.string.cukier, dolna = 70f, gorna = 99f),
    TEMPERATURA(title = R.string.temp, dolna = 35.5f, gorna = 37.5f),
    CISNIENIE_R (title = R.string.cisnienie_r, dolna = 80f, gorna = 84f),
    CISNIENIE_S (title = R.string.cisnienie_s, dolna = 120f, gorna = 129f),
    PULS (title = R.string.puls, dolna = 60f, gorna = 120f)
}


class CisnienieExamination (date: Date, cisnienieR : Float, cisnienieS: Float, patientId: Int)
{
    val examinationS = Examination(
        Patient_id = patientId,
        date = date,
        type = ExaminationType.CISNIENIE_S,
        value = cisnienieS
    )

    val examinationR = Examination(
        Patient_id = patientId,
        date = date,
        type = ExaminationType.CISNIENIE_R,
        value = cisnienieS
    )

}
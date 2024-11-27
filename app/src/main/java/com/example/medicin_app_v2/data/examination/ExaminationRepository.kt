package com.example.medicin_app_v2.data.examination

import kotlinx.coroutines.flow.Flow

interface ExaminationRepository {

    suspend fun insertExamination(examination: Examination) : Long

    suspend fun deleteExamination(examination: Examination)

    suspend fun updateExamination(examination: Examination)

    fun getExaminationById(id: Int): Flow<List<Examination>>

    fun getPatientsExaminations(patient_id: Int) : Flow<List<Examination>>

    fun getPatientsExaminationsType(patient_id: Int, type: ExaminationType) : Flow<List<Examination>>


}


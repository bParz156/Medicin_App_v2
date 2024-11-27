package com.example.medicin_app_v2.data.examination

import kotlinx.coroutines.flow.Flow

class OfflineExaminationRepository(private val examinationDao: ExaminationDao) : ExaminationRepository {
    override suspend fun insertExamination(examination: Examination): Long {
       return examinationDao.insert(examination)
    }

    override suspend fun deleteExamination(examination: Examination) {
        examinationDao.delete(examination)
    }

    override suspend fun updateExamination(examination: Examination) {
        examinationDao.update(examination)
    }

    override fun getExaminationById(id: Int): Flow<List<Examination>> {
        return examinationDao.getExaminationById(id)
    }

    override fun getPatientsExaminations(patient_id: Int): Flow<List<Examination>> {
        return examinationDao.getPatientsExaminations(patient_id)
    }

    override fun getPatientsExaminationsType(
        patient_id: Int,
        type: ExaminationType
    ): Flow<List<Examination>> {
        return examinationDao.getPatientsExaminationsType(patient_id, type)
    }
}
package com.example.medicin_app_v2.data.patient

import kotlinx.coroutines.flow.Flow

class OfflinePatientsRepository(private val patientDao: PatientDao) : PatientsRepository {
    override fun getAllPatientsStream(): Flow<List<Patient>> {
        return patientDao.getAllpatients()
    }

    override fun getPatientStream(id: Int): Flow<Patient?> {
        return  patientDao.getpatient(id)
    }

    override suspend fun insertPatient(patient: Patient) : Long {
        return patientDao.insert(patient = patient)
    }

    override suspend fun deletePatient(patient: Patient) {
        patientDao.delete(patient)
    }

    override suspend fun updatePatient(patient: Patient) {
        patientDao.update(patient)
    }
}
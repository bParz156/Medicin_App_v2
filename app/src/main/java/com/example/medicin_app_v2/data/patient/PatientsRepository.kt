package com.example.medicin_app_v2.data.patient

import kotlinx.coroutines.flow.Flow

interface PatientsRepository {

    fun getAllPatientsStream(): Flow<List<Patient>>


    fun getPatientStream(id: Int): Flow<Patient?>

    fun getPatientByName(name: String): Flow<Patient?>

    /**
     * Insert Patient in the data source
     */
    suspend fun insertPatient(patient: Patient) : Long

    /**
     * Delete Patient from the data source
     */
    suspend fun deletePatient(patient: Patient)

    /**
     * Update Patient in the data source
     */
    suspend fun updatePatient(patient: Patient)

}
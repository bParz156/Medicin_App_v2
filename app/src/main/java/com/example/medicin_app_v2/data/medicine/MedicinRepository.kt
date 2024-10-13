package com.example.medicin_app_v2.data.medicine

import kotlinx.coroutines.flow.Flow

interface MedicinRepository {

    /**
     * Retrieve all the Medicines from the the given data source.
     */
    fun getAllMedicinesStream(): Flow<List<Medicine>>

    /**
     * Retrieve an Medicine from the given data source that matches with the [id].
     */
    fun getMedicineStream(id: Int): Flow<Medicine?>

    /**
     * Insert Medicine in the data source
     */
    suspend fun insertMedicine(medicine: Medicine)

    /**
     * Delete Medicine from the data source
     */
    suspend fun deleteMedicine(medicine: Medicine)

    /**
     * Update Medicine in the data source
     */
    suspend fun updateMedicine(medicine: Medicine)

}
package com.example.medicin_app_v2.data.medicine

import com.example.medicin_app_v2.data.MedicinForm
import kotlinx.coroutines.flow.Flow

class OfflineMedicinRepository(private val medicineDao: MedicineDao) : MedicinRepository {
    override fun getAllMedicinesStream(): Flow<List<Medicine>> {
        return medicineDao.getAllmedicines()
    }

    override fun getMedicineStream(id: Int): Flow<Medicine?> {
        return medicineDao.getmedicine(id)
    }

    override fun getMedicineStream(name: String, form: MedicinForm): Flow<Medicine?> {
       return medicineDao.getmedicine(name, form)
    }

    override suspend fun insertMedicine(medicine: Medicine) {
        medicineDao.insert(medicine)
    }

    override suspend fun deleteMedicine(medicine: Medicine) {
        medicineDao.delete(medicine)
    }

    override suspend fun updateMedicine(medicine: Medicine) {
        medicineDao.update(medicine)
    }


}
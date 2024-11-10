package com.example.medicin_app_v2.data.storage

import kotlinx.coroutines.flow.Flow

class OfflineStorageRepository(private val storageDao: StorageDao) : StorageRepository {
    override fun getAllStoragesStream(): Flow<List<Storage>> {
        return storageDao.getAllStorages()
    }

    override fun getStorageStream(id: Int): Flow<Storage?> {
        return storageDao.getStorage(id)
    }

    override fun getAllMedicinesStorages(medicine_id: Int): Flow<List<Storage>> {
        return storageDao.getAllMedicinesStorages(medicine_id)
    }

    override suspend fun insertStorage(storage: Storage) : Long {
        return storageDao.insert(storage)
    }

    override suspend fun deleteStorage(storage: Storage) {
        storageDao.delete(storage)
    }

    override suspend fun updateStorage(storage: Storage) {
        storageDao.update(storage)
    }
}
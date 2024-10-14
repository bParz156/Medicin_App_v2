package com.example.medicin_app_v2.data.storage

import kotlinx.coroutines.flow.Flow

interface StorageRepository {

    fun getAllStoragesStream(): Flow<List<Storage>>


    fun getStorageStream(id: Int): Flow<Storage?>

    /**
     * Insert Storage in the data source
     */
    suspend fun insertStorage(storage: Storage)

    /**
     * Delete Storage from the data source
     */
    suspend fun deleteStorage(storage: Storage)

    /**
     * Update Storage in the data source
     */
    suspend fun updateStorage(storage: Storage)


}
package com.example.medicin_app_v2.data.firstAidKit

import kotlinx.coroutines.flow.Flow

interface FirstaidkitRepository {

    /**
     * Retrieve all the FirstAidKits from the the given patient with [id].
     */
    fun getAllFirstAidKitsStream(patient_id : Int): Flow<List<FirstAidKit>>

    /**
     * Insert FirstAidKit in the data source
     */
    suspend fun insertFirstAidKit(firstAidKit: FirstAidKit)

    /**
     * Delete FirstAidKit from the data source
     */
    suspend fun deleteFirstAidKit(firstAidKit: FirstAidKit)

    /**
     * Update FirstAidKit in the data source
     */
    suspend fun updateFirstAidKit(firstAidKit: FirstAidKit)

}
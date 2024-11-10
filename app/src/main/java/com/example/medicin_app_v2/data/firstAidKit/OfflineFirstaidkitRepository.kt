package com.example.medicin_app_v2.data.firstAidKit

import kotlinx.coroutines.flow.Flow

class OfflineFirstaidkitRepository(private val firstAidKitDao: FirstAidKitDao) : FirstaidkitRepository {
    override fun getAllFirstAidKitsStream(patientId: Int): Flow<List<FirstAidKit>> {
        return firstAidKitDao.getpatientsfirstAidKit(patientId)
    }

    override fun getfirstAidKitByStorage(storage_id: Int): Flow<List<FirstAidKit>> {
        return  firstAidKitDao.getfirstAidKitByStorage(storage_id)
    }


    override suspend fun insertFirstAidKit(firstAidKit: FirstAidKit) : Long {
        return firstAidKitDao.insert(firstAidKit)
    }

    override suspend fun deleteFirstAidKit(firstAidKit: FirstAidKit) {
        firstAidKitDao.delete(firstAidKit)
    }

    override suspend fun updateFirstAidKit(firstAidKit: FirstAidKit) {
        firstAidKitDao.update(firstAidKit)
    }
}
package com.example.medicin_app_v2.data.notification

import kotlinx.coroutines.flow.Flow

class OfflineNotificationRepository(private val notificationDao: NotificationDao) : NotificationRepository {
    override fun getAllNotificationsStream(): Flow<List<Notification>> {
        return notificationDao.getAllnotification()
    }

    override fun getAllNotificationsUsageStream(usage_id: Int): Flow<List<Notification>> {
        return notificationDao.getAllnotificationABoutUsage(usage_id)
    }

    override fun getNotificationStream(id: Int): Flow<Notification?> {
        return notificationDao.getNotification(id)
    }

    override suspend fun insertNotification(notification: Notification) {
        notificationDao.insert(notification)
    }

    override suspend fun deleteNotification(notification: Notification) {
        notificationDao.delete(notification)
    }

    override suspend fun updateNotification(notification: Notification) {
        notificationDao.update(notification)
    }
}
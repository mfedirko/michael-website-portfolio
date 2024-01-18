package io.mfedirko.contactme.notification

interface NotificationPreferenceRepository {
    fun getNotificationPreference(): NotificationPreference?
    fun updateNotificationPreference(notificationPreference: NotificationPreference)
}
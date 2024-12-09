package com.example.medicin_app_v2

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.provider.Settings
import androidx.annotation.RequiresApi
import androidx.compose.ui.res.stringResource
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import com.example.medicin_app_v2.data.AppContainer
import com.example.medicin_app_v2.data.AppDataContainer

private const val TAG = "UsageWorker"

/**
 * Aplikcacja
 */
class MedicinApplication : Application() {

    /**
     * AppContainer instance used by the rest of classes to obtain dependencies
     */
    lateinit var container: AppContainer
  //  lateinit var userPreferencesRepository: UserPreferencesRepository

    /**
     * W momencie tworzenia aplikacji zapełniany jest kontener aplikacji - dostęp do repozytoriów z bazą danych, preferencjami użytkownika oraz zarządzaniem zadaniami w tle
     */
    override fun onCreate() {
        super.onCreate()
        container = AppDataContainer(this)
        createNotificationChannel()
        createChanelForAlarms(this)
      //  userPreferencesRepository = UserPreferencesRepository(dataStore)

    }

    /**
     * Tworzenie kaznłu powiadomień
     */
    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channelId = "example_channel_id"
            val channelName = "Powiadomienia_push"
            val importance = NotificationManager.IMPORTANCE_HIGH
            val channel = NotificationChannel(channelId, channelName, importance)
            channel.description = "Powiadomienia o kończących się zapasach leków będą wyświetlane, aby przypomnieć o konieczności uzupełnienia zapasów"

            val notificationManager = getSystemService(NotificationManager::class.java)
            notificationManager?.createNotificationChannel(channel)
        }
    }

    /**
     * Tworzenie kanału dla alarmów - na przyszłość
     */
    private fun createChanelForAlarms(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {  // Adjusted to Android 8.0 and above
            val channelId = "alarm_id"
            val channelName = "Alarm Notifications"
            val importance = NotificationManager.IMPORTANCE_HIGH
            val channel = NotificationChannel(channelId, channelName, importance).apply {
                description = "Channel for Alarm Notifications"
                enableLights(true)
                lightColor = Color.RED
                enableVibration(true)
                setShowBadge(true)
            }

            val notificationManager = context.getSystemService(NotificationManager::class.java)
            notificationManager?.createNotificationChannel(channel)
//            val intent = Intent(Settings.ACTION_IGNORE_BATTERY_OPTIMIZATION_SETTINGS)
//            startActivity(intent)
        }


    }
}

private const val PREFERENCES_NAME = "user_preferences"
val Context.dataStore: DataStore<Preferences> by preferencesDataStore(
    name = PREFERENCES_NAME
)
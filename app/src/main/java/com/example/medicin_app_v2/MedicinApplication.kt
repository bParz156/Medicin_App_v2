package com.example.medicin_app_v2

import android.app.Application
import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import com.example.medicin_app_v2.data.AppContainer
import com.example.medicin_app_v2.data.AppDataContainer
import com.example.medicin_app_v2.data.UserPreferencesRepository

class MedicinApplication : Application() {

    /**
     * AppContainer instance used by the rest of classes to obtain dependencies
     */
    lateinit var container: AppContainer
  //  lateinit var userPreferencesRepository: UserPreferencesRepository

    override fun onCreate() {
        super.onCreate()
        container = AppDataContainer(this)
      //  userPreferencesRepository = UserPreferencesRepository(dataStore)
    }
}

private const val PREFERENCES_NAME = "user_preferences"
val Context.dataStore: DataStore<Preferences> by preferencesDataStore(
    name = PREFERENCES_NAME
)
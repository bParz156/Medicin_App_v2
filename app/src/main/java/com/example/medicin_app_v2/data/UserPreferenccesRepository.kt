package com.example.medicin_app_v2.data

import android.util.Log
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException

enum class ThemeMode {
    DARK_MODE, DAY_MODE, HIGH_CONTRAST
}

class UserPreferencesRepository(
    private val dataStore: DataStore<Preferences>
) {
    val themeMode: Flow<ThemeMode> = dataStore.data
        .catch {
            if(it is IOException) {
                Log.e(TAG, "Error reading preferences.", it)
                emit(emptyPreferences())
            } else {
                throw it
            }
        }
        .map { preferences ->
            val modeString = preferences[THEME_MODE] ?: ThemeMode.DAY_MODE.name
            ThemeMode.valueOf(modeString) // Convert stored string to ThemeMode
        }


    suspend fun saveThemeMode(mode: ThemeMode) {
        dataStore.edit { preferences ->
            preferences[THEME_MODE] = mode.name // Save the enum as a string
        }
    }

    private companion object {
        val THEME_MODE = stringPreferencesKey("theme_mode")
        const val TAG = "UserPreferencesRepo"
    }
}
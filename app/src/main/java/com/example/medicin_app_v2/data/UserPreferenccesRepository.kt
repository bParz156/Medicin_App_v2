package com.example.medicin_app_v2.data

import android.content.Context
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.floatPreferencesKey
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.preferencesOf
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import java.io.IOException

enum class ThemeMode {
    DARK_MODE, DAY_MODE, HIGH_CONTRAST
}

/**
 * Preferencje użytkownika (kontarst, wybrany pacjent (jego id) oraz wielkość czcionki)
 */
class UserPreferencesRepository(
    private val dataStore: DataStore<Preferences>
) {
    val themeMode: Flow<ThemeMode> = dataStore.data
//        .catch {
//            if(it is IOException) {
//                Log.e(TAG, "Error reading preferences.", it)
//                emit(emptyPreferences())
//            } else {
//                throw it
//            }
//        }
        .map { preferences ->
            val modeString = preferences[THEME_MODE] ?: ThemeMode.DAY_MODE.name
            ThemeMode.valueOf(modeString) // Convert stored string to ThemeMode
        }


    val patient_id: Flow<Int> = dataStore.data
        .catch {
            if(it is IOException) {
                Log.e(TAG, "Error reading preferences ID:.", it)
                emit(emptyPreferences())
            } else {
                throw it
            }
        }
        .map {
            preferences ->
             val id=   preferences[PATIENT_ID] ?: -1
            id
        }

    val fontScale: Flow<Float> = dataStore.data
        .map {
            preferences ->
             val scale  = preferences[FONT_SCALE] ?: 1f
            scale
        }


    suspend fun saveThemeMode(mode: ThemeMode) {
        dataStore.edit { preferences ->
            preferences[THEME_MODE] = mode.name // Save the enum as a string
        }
    }

    suspend fun savePatientId(id: Int)
    {
        Log.i("patientId", "przed zmiana: ${patient_id.first()}?")
        dataStore.edit {
            preferences ->
            preferences[PATIENT_ID]= id
        }
        Log.i("patientId", "po zmianie: ${patient_id.first()}?")
    }

    suspend fun saveFontScale(scale: Float)
    {
        dataStore.edit { preferences ->
            preferences[FONT_SCALE] =scale // Save the enum as a string
        }

    }

    fun  updateFontScale(context: Context, scale: Float): Context {
        val configuration = context.resources.configuration
        configuration.fontScale = scale // Skala, np. 1.0 = 100%, 1.2 = 120%, 0.8 = 80%

        return context.createConfigurationContext(configuration)
    }

    suspend fun updateFontScaleAndRecreate(context: Context, scale: Float, activity: ComponentActivity) {
        val updatedContext = updateFontScale(context, scale)
        dataStore.edit { preferences -> preferences[FONT_SCALE] = scale }
        activity.recreate() // Odtwórz aktywność z nowym kontekstem
    }



    private companion object {
        val THEME_MODE = stringPreferencesKey("theme_mode")
        val PATIENT_ID = intPreferencesKey("patient_id")
        val FONT_SCALE = floatPreferencesKey("font_scale")
        const val TAG = "UserPreferencesRepo"
    }
}
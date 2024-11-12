package com.yousuf.weatherapp

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.yousuf.weatherapp.provider.DispatcherProvider
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import javax.inject.Inject

/**
 * Preference provider for the app that persists last known location info
 */
class AppPrefs(
    private val context: Context,
    private val dispatchers: DispatcherProvider
) {

    val Context.dataStore by preferencesDataStore(
        name = LOCATION_PREFERENCES_NAME
    )

    val cityFlow: Flow<String> = context.dataStore.data
        .catch { exception -> emit(emptyPreferences()) }
        .map { preferences -> preferences[CITY_PREF] ?: "" }

    suspend fun updateShowCompleted(city: String) {
        runCatching {
            withContext(dispatchers.io) {
                context.dataStore.edit { preferences ->
                    preferences[CITY_PREF] = city
                }
            }
        }.onFailure {
            //TODO perform any analytics or logging upon failures
        }
    }

    companion object {
        private const val LOCATION_PREFERENCES_NAME = "location-preferences"
        private val CITY_PREF = stringPreferencesKey("city")
    }
}
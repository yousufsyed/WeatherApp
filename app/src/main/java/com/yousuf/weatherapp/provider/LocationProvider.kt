package com.yousuf.weatherapp.provider

import com.yousuf.weatherapp.AppPrefs
import com.yousuf.weatherapp.network.data.GeoLocation
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.merge
import kotlinx.coroutines.flow.update
import javax.inject.Inject

interface LocationProvider {
    fun locationFlow(): Flow<String>
    fun updateLocation(city: String)
    fun addLocation(location: GeoLocation)
    suspend fun saveCityToPrefs(city: String)
}

class DefaultLocationProvider @Inject constructor(
    private val appPrefs: AppPrefs,
    private val locationDataSource: GeoLocationDataSource
) : LocationProvider {
    var searchQuery = MutableStateFlow<String>("")

    override fun locationFlow(): Flow<String> {
        return merge(appPrefs.cityFlow, searchQuery)
    }

    override fun updateLocation(city: String) {
        searchQuery.update { city }
    }

    override fun addLocation(location: GeoLocation) {
        updateLocation(location.city)
        locationDataSource.addGeoLocation(location)
    }

    override suspend fun saveCityToPrefs(city: String) {
        appPrefs.updateShowCompleted(city)
    }
}
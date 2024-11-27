package com.yousuf.weatherapp.provider

import com.yousuf.weatherapp.network.GeoLocationClient
import com.yousuf.weatherapp.network.data.GeoLocation
import kotlinx.coroutines.withContext
import javax.inject.Inject

interface GeoLocationProvider {
    suspend fun getGeoLocation(city: String): GeoLocation
}

/**
 * Provider responsible for providing geo location data
 */
class GeoLocationProviderImpl @Inject constructor(
    private val geoLocationClient: GeoLocationClient,
    private val geoLocationDataSource: GeoLocationDataSource,
    private val dispatcherProvider: DispatcherProvider
) : GeoLocationProvider {

    // fetch geo location either from cache or api call
    // if fetched from api also add the data to geoLocationCache,
    // so it can be reused for later calls
    override suspend fun getGeoLocation(city: String): GeoLocation {
        return withContext(dispatcherProvider.io) {
            geoLocationDataSource.getGeoLocation(city) ?: geoLocationClient.fetchGeoLocation(city).apply {
                geoLocationDataSource.addGeoLocation(this)
            }
        }
    }
}
package com.yousuf.weatherapp.provider

import com.yousuf.weatherapp.network.GeoLocationClient
import com.yousuf.weatherapp.network.data.GeoLocation
import kotlinx.coroutines.withContext
import javax.inject.Inject

interface GeoLocationProvider {
    fun updateGeoLocation(location: GeoLocation)

    suspend fun getGeoLocation(city: String): GeoLocation
}

/**
 * Provider responsible for providing geo location data
 */
class GeoLocationProviderImpl @Inject constructor(
    private val geoLocationClient: GeoLocationClient,
    private val dispatcherProvider: DispatcherProvider,
) : GeoLocationProvider {

    // cache for temporarily holding geo location
    private val geoLocationCache = mutableMapOf<String, GeoLocation>()

    // update geo location received from the Location service
    override fun updateGeoLocation(location: GeoLocation) {
        geoLocationCache[location.city] = location
    }

    // fetch geo location either from cache or api call
    // if fetched from api also add the data to geoLocationCache,
    // so it can be reused for later calls
    override suspend fun getGeoLocation(city: String): GeoLocation {
        return withContext(dispatcherProvider.io) {
            geoLocationCache[city] ?: geoLocationClient.fetchGeoLocation(city).apply {
                geoLocationCache[city] = this
            }
        }
    }

}
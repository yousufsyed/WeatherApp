package com.yousuf.weatherapp.provider

import com.yousuf.weatherapp.network.data.GeoLocation
import javax.inject.Inject

interface GeoLocationDataSource {
    fun addGeoLocation(location: GeoLocation)
    fun getGeoLocation(city: String): GeoLocation?
}

/**
 * Data source responsible for holding geo location data
 */
class DefaultGeoLocationDataSource @Inject constructor() : GeoLocationDataSource {

    // cache for temporarily holding geo location
    private val geoLocationCache = mutableMapOf<String, GeoLocation>()

    // update geo location received from the Location service
    override fun addGeoLocation(location: GeoLocation) {
        geoLocationCache[location.city] = location
    }

    override fun getGeoLocation(city: String): GeoLocation? = geoLocationCache[city]
}
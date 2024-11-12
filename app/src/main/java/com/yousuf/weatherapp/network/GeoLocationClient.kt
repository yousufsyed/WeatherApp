package com.yousuf.weatherapp.network

import com.yousuf.weatherapp.BuildConfig
import com.yousuf.weatherapp.network.WeatherService.Companion.KEY_APP_ID
import com.yousuf.weatherapp.network.WeatherService.Companion.KEY_LIMIT
import com.yousuf.weatherapp.network.WeatherService.Companion.KEY_QUERY
import com.yousuf.weatherapp.network.data.GeoLocation
import com.yousuf.weatherapp.network.data.toGeoLocation
import com.yousuf.weatherapp.provider.DispatcherProvider
import kotlinx.coroutines.withContext
import javax.inject.Inject

interface GeoLocationClient {
    suspend fun fetchGeoLocation(city: String): GeoLocation
}

class DefaultGeoLocationClient @Inject constructor(
    private val dispatcherProvider: DispatcherProvider,
    private val weatherService: WeatherService
) : GeoLocationClient {

    // fetch geo location from api
    override suspend fun fetchGeoLocation(city: String): GeoLocation {
        return withContext(dispatcherProvider.io) {
            weatherService.getGeoLocation(
                mapOf(
                    KEY_QUERY to city,
                    KEY_LIMIT to "1", // Set limit=1, to get only one result per city name
                    KEY_APP_ID to BuildConfig.WEATHER_API_KEY
                )
            ).let { response ->
                if(response.isSuccessful && response.body() != null) {
                    response.body()!!.toGeoLocation()
                } else {
                    throw GeoLocationException()
                }
            }
        }
    }
}

class GeoLocationException() : RuntimeException("Error fetching geo location")
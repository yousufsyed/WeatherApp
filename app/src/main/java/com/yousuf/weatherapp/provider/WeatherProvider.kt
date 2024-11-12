package com.yousuf.weatherapp.provider

import com.yousuf.weatherapp.network.WeatherClient
import com.yousuf.weatherapp.network.data.GeoLocation
import com.yousuf.weatherapp.network.data.WeatherData
import kotlinx.coroutines.withContext
import javax.inject.Inject

interface WeatherProvider {
    suspend fun getWeatherData(geoLocation: GeoLocation): WeatherData
}

/**
 * Provider for providing weather data.
 */
class WeatherProviderImpl @Inject constructor(
    private val dispatcherProvider: DispatcherProvider,
    private val weatherClient: WeatherClient,
) : WeatherProvider {

    // cache for temporarily holding weather data
    private val weatherDataCache = mutableMapOf<String, WeatherData>()

    // fetch weather data either from cache or api call
    // if fetched from api also add the data to weatherDataCache,
    // so it can be reused for later calls
    override suspend fun getWeatherData(geoLocation: GeoLocation): WeatherData {
        return withContext(dispatcherProvider.io) {
            weatherDataCache[geoLocation.city] ?: weatherClient.fetchWeatherData(geoLocation)
                .apply {
                    weatherDataCache[geoLocation.city] = this
                }
        }
    }
}
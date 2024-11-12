package com.yousuf.weatherapp.network

import com.yousuf.weatherapp.BuildConfig
import com.yousuf.weatherapp.network.WeatherService.Companion.KEY_APP_ID
import com.yousuf.weatherapp.network.WeatherService.Companion.KEY_LAT
import com.yousuf.weatherapp.network.WeatherService.Companion.KEY_LON
import com.yousuf.weatherapp.network.data.GeoLocation
import com.yousuf.weatherapp.network.data.WeatherData
import com.yousuf.weatherapp.network.data.toWeatherData
import com.yousuf.weatherapp.provider.DispatcherProvider
import kotlinx.coroutines.withContext
import javax.inject.Inject

interface WeatherClient {
    suspend fun fetchWeatherData(geoLocation: GeoLocation): WeatherData
}

/***
 * Network client to fetch weather data from api
 */
class DefaultWeatherClient @Inject constructor(
    private val dispatcherProvider: DispatcherProvider,
    private val weatherService: WeatherService
) : WeatherClient {

    // fetch weather data from api
    override suspend fun fetchWeatherData(geoLocation: GeoLocation): WeatherData {
        return withContext(dispatcherProvider.io) {
            weatherService.getWeatherByCityName(
                mapOf(
                    KEY_LAT to geoLocation.lat,
                    KEY_LON to geoLocation.lon,
                    KEY_APP_ID to BuildConfig.WEATHER_API_KEY
                )
            ).let { response ->
                if(response.isSuccessful && response.body() != null) {
                    response.body()!!.toWeatherData()
                } else {
                    // for now just throwing an exception if the api response is failure,
                    // this can be refined to handle different type of errors
                    throw WeatherDataException()
                }
            }
        }
    }
}

class WeatherDataException() : RuntimeException("Error fetching weather data")
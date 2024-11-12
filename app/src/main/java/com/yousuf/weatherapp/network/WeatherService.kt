package com.yousuf.weatherapp.network

import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.QueryMap

interface WeatherService {

    @GET("/data/2.5/weather")
    suspend fun getWeatherByCityName(@QueryMap queries: Map<String, String>): Response<ResponseBody>

    @GET("/geo/1.0/direct")
    suspend fun getGeoLocation(@QueryMap queries: Map<String, String>): Response<ResponseBody>

    companion object {
        const val KEY_LON = "lon"
        const val KEY_LAT = "lat"
        const val KEY_APP_ID = "appid"
        const val KEY_QUERY = "q"
        const val KEY_LIMIT = "limit"
    }
}
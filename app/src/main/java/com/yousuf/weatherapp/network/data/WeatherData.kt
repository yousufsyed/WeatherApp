package com.yousuf.weatherapp.network.data

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import okhttp3.ResponseBody
import org.json.JSONObject
import java.math.BigDecimal
import java.math.RoundingMode

/**
 * Data class for holding weather data.
 */
@Parcelize
data class WeatherData(
    val temp: String,
    val feelsLike: String?,
    val tempMin: String,
    val tempMax: String,
    val pressure: Int?,
    val humidity: Int?,
    val weather: String?,
    val description: String?,
    val icon: String?
) : Parcelable {

    val iconUrl: String
        get() = "https://openweathermap.org/img/wn/$icon@2x.png"
}

// converter to convert necessary weather data from response body
fun ResponseBody.toWeatherData() = run {
    try {
        val body = string()
        val response = JSONObject(body)
        val weatherData = response.optJSONArray("weather")?.optJSONObject(0)

        response.optJSONObject("main")?.let {
            WeatherData(
                temp = it.optDouble("temp").round().toString(),
                feelsLike = it.optDouble("feels_like").round().toString(),
                tempMax = it.optDouble("temp_max").round().toString(),
                tempMin = it.optDouble("temp_min").round().toString(),
                pressure = it.optInt("pressure"),
                humidity = it.optInt("humidity"),
                weather = weatherData?.optString("main"),
                description = weatherData?.optString("description"),
                icon = weatherData?.optString("icon")
            )
        } ?: throw WeatherDataParseException()
    } catch (e: Exception) {
        // catching all exceptions,
        // this can be improved to handle specific exceptions and return appropriate error messages
        throw WeatherDataParseException()
    }
}

// extension for rounding double values to 2 precisions
fun Double.round(): Double {
    return BigDecimal(this).setScale(2, RoundingMode.HALF_EVEN).toDouble()
}

class WeatherDataParseException() : Exception("Failed to parse weather data")
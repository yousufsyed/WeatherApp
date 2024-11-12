package com.yousuf.weatherapp.network.data

import okhttp3.ResponseBody
import org.json.JSONArray

/**
 * Data class for holding geo location
 */
data class GeoLocation(
    val city: String,
    val lat: String,
    val lon: String,
    val country: String?,
    val state: String?
)

// converter to convert necessary geo location data from response body
fun ResponseBody.toGeoLocation() = run {
    try {
        JSONArray(this.string()).optJSONObject(0)?.let { location ->
            GeoLocation(
                city = location.optString("name"),
                lat = location.optString("lat"),
                lon = location.optString("lon"),
                country = location.optString("country"),
                state = location.optString("state")
            )
        } ?: throw GeoLocationParseException()
    } catch (e: Exception) {
        // catching all exceptions,
        // this can be improved to handle specific exceptions and return appropriate error messages
        throw GeoLocationParseException()
    }
}

class GeoLocationParseException() : Exception("Failed to parse geo location")
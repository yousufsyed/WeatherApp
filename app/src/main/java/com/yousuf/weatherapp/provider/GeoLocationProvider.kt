package com.yousuf.weatherapp.provider

import android.annotation.SuppressLint
import android.content.Context
import android.location.Geocoder
import com.google.android.gms.location.LocationServices
import com.yousuf.weatherapp.network.GeoLocationClient
import com.yousuf.weatherapp.network.data.GeoLocation
import kotlinx.coroutines.withContext
import java.util.Locale
import javax.inject.Inject
import kotlin.collections.isNotEmpty

interface GeoLocationProvider {
    fun updateGeoLocation(location: GeoLocation)

    suspend fun getGeoLocation(city: String): GeoLocation

    fun getCurrentLocation(
        context: Context,
        onLocationReceived: (String) -> Unit,
        onFailureListener: (Context) -> Unit
    )
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

    @SuppressLint("MissingPermission")
    override fun getCurrentLocation(
        context: Context,
        onLocationReceived: (String) -> Unit,
        onFailureListener: (Context) -> Unit
    ) {
        val fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)
        fusedLocationClient.lastLocation
            .addOnSuccessListener { location ->
                if (location != null) {
                    val lat = location.latitude
                    val lon = location.longitude
                    val geoCoder = Geocoder(context, Locale.getDefault())
                    val addresses = geoCoder.getFromLocation(lat, lon, 1)
                    if (addresses != null && addresses.isNotEmpty()) {
                        updateGeoLocation(
                            GeoLocation(
                                lat = lat.toString(),
                                lon = lon.toString(),
                                city = addresses[0].locality,
                                state = addresses[0].adminArea,
                                country = addresses[0].countryName
                            )
                        )
                        onLocationReceived(addresses[0].locality)
                    } else {
                        onFailureListener(context)
                    }
                }
            }
            .addOnFailureListener { exception ->
                // Handle location retrieval failure
                onFailureListener(context)
            }
    }

}
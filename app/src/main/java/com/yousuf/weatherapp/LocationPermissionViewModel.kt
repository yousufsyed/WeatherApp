package com.yousuf.weatherapp

import android.Manifest.permission.ACCESS_COARSE_LOCATION
import android.Manifest.permission.ACCESS_FINE_LOCATION
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager.PERMISSION_GRANTED
import android.location.Geocoder
import androidx.compose.runtime.mutableStateOf
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.location.LocationServices
import com.yousuf.weatherapp.LocationState.Denied
import com.yousuf.weatherapp.LocationState.Error
import com.yousuf.weatherapp.LocationState.Granted
import com.yousuf.weatherapp.LocationState.RequestPermission
import com.yousuf.weatherapp.LocationState.ShowRational
import com.yousuf.weatherapp.LocationState.Success
import com.yousuf.weatherapp.network.data.GeoLocation
import com.yousuf.weatherapp.provider.DispatcherProvider
import com.yousuf.weatherapp.provider.LocationProvider
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.Locale
import javax.inject.Inject

@HiltViewModel
class LocationPermissionViewModel @Inject constructor(
    private val locationProvider: LocationProvider,
    private val dispatchers: DispatcherProvider
) : ViewModel() {

    private val _permissionState = MutableStateFlow<LocationState>(ShowRational)
    val permissionState = _permissionState.asStateFlow()

    var shouldShowRationalDialog = mutableStateOf(true)
        private set

    fun onPermissionGranted(context: Context) {
        disablePermissionRationalDialog()
        _permissionState.value = if (context.hasLocationPermission) Granted else RequestPermission
    }

    fun onPermissionResults(permissions: Map<String, Boolean>) {
        viewModelScope.launch(dispatchers.io) {
            _permissionState.update {
                val isGranted = permissions.values.reduce { acc, isPermissionGranted ->
                    acc && isPermissionGranted
                }
                if (isGranted) Granted else Denied
            }
        }
    }

    @SuppressLint("MissingPermission")
    fun getCurrentLocation(context: Context) {
        viewModelScope.launch(dispatchers.io) {
            val fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)
            fusedLocationClient.lastLocation
                .addOnSuccessListener { location ->
                    //disable dialog
                    disablePermissionRationalDialog()

                    // check/fetch location and update the state accordingly
                    if (location != null) {
                        Geocoder(context, Locale.getDefault())
                            .getFromLocation(location.latitude, location.longitude, 1)
                            ?.take(1)
                            ?.map { geoCoder ->
                                val geoLocation = GeoLocation(
                                    lat = location.latitude.toString(),
                                    lon = location.longitude.toString(),
                                    city = geoCoder.locality,
                                    state = geoCoder.adminArea,
                                    country = geoCoder.countryName
                                )
                                locationProvider.addLocation(geoLocation)
                                _permissionState.update { Success }
                                return@addOnSuccessListener
                            }
                    }
                    // Handle location retrieval failure
                    _permissionState.update { Error }
                }
                .addOnFailureListener { exception ->
                    // Handle location retrieval failure
                    _permissionState.update { Error }
                    disablePermissionRationalDialog()
                }
        }
    }

    fun disablePermissionRationalDialog() {
        shouldShowRationalDialog.value = false
    }
}

sealed class LocationState {
    data object ShowRational : LocationState()
    data object Error : LocationState()
    data object Success : LocationState()
    data object Granted : LocationState()
    data object Denied : LocationState()
    data object RequestPermission : LocationState()
}

val Context.hasLocationPermission: Boolean
    get() = ContextCompat.checkSelfPermission(this, ACCESS_FINE_LOCATION) == PERMISSION_GRANTED &&
            ContextCompat.checkSelfPermission(this, ACCESS_COARSE_LOCATION) == PERMISSION_GRANTED
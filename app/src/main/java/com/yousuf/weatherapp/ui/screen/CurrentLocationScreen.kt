package com.yousuf.weatherapp.ui.screen

import android.Manifest.permission.ACCESS_COARSE_LOCATION
import android.Manifest.permission.ACCESS_FINE_LOCATION
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager.PERMISSION_GRANTED
import android.location.Geocoder
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.StringRes
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat.checkSelfPermission
import androidx.hilt.navigation.compose.hiltViewModel
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.android.gms.location.LocationServices
import com.yousuf.weatherapp.R
import com.yousuf.weatherapp.WeatherViewModel
import com.yousuf.weatherapp.network.data.GeoLocation
import java.util.Locale
import kotlin.collections.isNotEmpty

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun ShowRationalDialog(
    viewModel: WeatherViewModel = hiltViewModel()
) {
    val permissions = listOf<String>(ACCESS_COARSE_LOCATION, ACCESS_FINE_LOCATION)
    val context = LocalContext.current

    val requestPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions(),
        onResult = { permissions ->
            val isGranted = permissions.values.reduce { acc, isPermissionGranted ->
                acc && isPermissionGranted
            }
            if (isGranted) {
                getCurrentLocation(
                    context,
                    { location -> viewModel.updateLocation(location) },
                    ::onLocationFetchFailed
                )
            }
        }
    )
    val showDialog = remember { viewModel.showDialog }
    if (showDialog.value) {
        AlertDialogScreen(
            title = "Location Permission",
            message = "This app requires location permission to function properly.",
            onDismissed = { viewModel.disablePermissionsDialog() },
            onConfirmed = {
                viewModel.disablePermissionsDialog()
                if (hasLocationPermission(context)) {
                    // Permission already granted, update the location
                    getCurrentLocation(
                        context,
                        { location -> viewModel.updateLocation(location) },
                        ::onLocationFetchFailed
                    )
                } else {
                    requestPermissionLauncher.launch(permissions.toTypedArray()) // Request location permission
                }
            }
        )
    }
}

/**
 * Handle location fetch failed, let the user know that he needs to enter location manually.
 */
private fun onLocationFetchFailed(context: Context) {
    showToast(context, R.string.location_fetch_failed)
}

private fun showToast(context: Context, @StringRes messageId: Int) {
    Toast.makeText(context, messageId, Toast.LENGTH_SHORT).show()
}

@SuppressLint("MissingPermission")
fun getCurrentLocation(
    context: Context,
    onLocationReceived: (GeoLocation) -> Unit,
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
                    onLocationReceived(
                        GeoLocation(
                            lat = lat.toString(),
                            lon = lon.toString(),
                            city = addresses[0].locality,
                            state = addresses[0].adminArea,
                            country = addresses[0].countryName
                        )
                    )
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

fun hasLocationPermission(context: Context): Boolean {
    return checkSelfPermission(context, ACCESS_FINE_LOCATION) == PERMISSION_GRANTED &&
            checkSelfPermission(context, ACCESS_COARSE_LOCATION) == PERMISSION_GRANTED
}


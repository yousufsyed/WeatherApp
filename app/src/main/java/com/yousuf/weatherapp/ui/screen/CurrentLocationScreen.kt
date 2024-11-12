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
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat.checkSelfPermission
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.android.gms.location.LocationServices
import com.yousuf.weatherapp.R
import com.yousuf.weatherapp.network.data.GeoLocation
import java.util.Locale

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun ShowRationalDialog(
    action: (GeoLocation) -> Unit,
) {
    val permissions = listOf<String>(ACCESS_COARSE_LOCATION, ACCESS_FINE_LOCATION)
    val showRationalDialog = remember { mutableStateOf(false) }
    val context = LocalContext.current

    val requestPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions(),
        onResult = { permissions ->
            val isGranted = permissions.values.reduce { acc, isPermissionGranted ->
                acc && isPermissionGranted
            }
            if (isGranted) {
                getCurrentLocation(context, action, ::onLocationFetchFailed)
            } else {
                onPermissionDenied(context)
            }
        }
    )

    if (showRationalDialog.value) {
        AlertDialog(
            onDismissRequest = {
                showRationalDialog.value = false
            },
            title = {
                Text(
                    text = "Location Permission",
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
            },
            text = {
                Text(
                    "Please grant location permission to populate location automatically",
                    fontSize = 16.sp
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        showRationalDialog.value = false
                        if (context.hasLocationPermission()) {
                            // Permission already granted, update the location
                            getCurrentLocation(context, action, ::onLocationFetchFailed)
                        } else {
                            requestPermissionLauncher.launch(permissions.toTypedArray()) // Request location permission
                        }
                    }
                ) {
                    Text("OK")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { showRationalDialog.value = false }
                ) {
                    Text("Cancel")
                }
            },
        )
    }
}

/**
 * Handle permission denied, let the user know that he needs to enter location manually.
 */
private fun onPermissionDenied(context: Context) {
    showToast(context, R.string.permission_denied)
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
private fun getCurrentLocation(
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
            Toast.makeText(
                context,
                "Can't get your Location. Please enter manually.",
                Toast.LENGTH_SHORT
            ).show()
        }
}

private fun Context.hasLocationPermission(): Boolean {
    return checkSelfPermission(this, ACCESS_FINE_LOCATION) == PERMISSION_GRANTED &&
            checkSelfPermission(this, ACCESS_COARSE_LOCATION) == PERMISSION_GRANTED
}
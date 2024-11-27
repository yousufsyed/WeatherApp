package com.yousuf.weatherapp.ui.screen

import android.Manifest.permission.ACCESS_COARSE_LOCATION
import android.Manifest.permission.ACCESS_FINE_LOCATION
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.yousuf.weatherapp.LocationPermissionViewModel
import com.yousuf.weatherapp.LocationState.Denied
import com.yousuf.weatherapp.LocationState.Error
import com.yousuf.weatherapp.LocationState.Granted
import com.yousuf.weatherapp.LocationState.RequestPermission
import com.yousuf.weatherapp.R
import com.yousuf.weatherapp.provider.MessageDelegate.Companion.showSnackbar
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun LocationRationalDialog(
    locationViewModel: LocationPermissionViewModel = hiltViewModel<LocationPermissionViewModel>(key="location")
) {
    val permissions by lazy {
        listOf<String>(ACCESS_COARSE_LOCATION, ACCESS_FINE_LOCATION)
    }

    val context = LocalContext.current

    val requestPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions(),
        onResult = { locationViewModel::onPermissionResults }
    )

    val locationStateModel = locationViewModel.permissionState.collectAsStateWithLifecycle()

    val locationState by remember { locationStateModel }

    when (locationState) {
        Error -> showSnackbar(context.getString(R.string.location_fetch_failed))
        Denied -> showSnackbar(context.getString(R.string.permission_denied))
        Granted -> locationViewModel.getCurrentLocation(context)
        RequestPermission -> requestPermissionLauncher.launch(permissions.toTypedArray())
        else -> PermissionDialog(locationViewModel)
    }
}
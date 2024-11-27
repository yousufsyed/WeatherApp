package com.yousuf.weatherapp.ui.screen

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.yousuf.weatherapp.LocationPermissionViewModel
import com.yousuf.weatherapp.R

@Composable
fun PermissionDialog(
    locationViewModel: LocationPermissionViewModel = hiltViewModel<LocationPermissionViewModel>(key="location")
) {
    val context = LocalContext.current
    val showDialog = remember("showDialog") { locationViewModel.shouldShowRationalDialog }
    AnimatedVisibility(showDialog.value) {
        AlertDialog(
            title = {
                Text(
                    text = stringResource(R.string.location_permission_title),
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
            },
            text = {
                Text(
                    text = stringResource(R.string.location_permission_message),
                    fontSize = 12.sp
                )
            },
            onDismissRequest = {
                locationViewModel.disablePermissionRationalDialog()
            },
            dismissButton = {
                TextButton(onClick = { locationViewModel.disablePermissionRationalDialog() }) {
                    Text(text = stringResource(R.string.cancel))
                }
            },
            confirmButton = {
                TextButton(onClick = {
                    locationViewModel.onPermissionGranted(context)
                }) {
                    Text(text = stringResource(R.string.ok))
                }
            }
        )
    }
}
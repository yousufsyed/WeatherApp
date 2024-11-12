package com.yousuf.weatherapp.ui.screen

import android.content.res.Configuration
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
import com.yousuf.weatherapp.ui.theme.WeatherAppTheme
import com.yousuf.weatherapp.R

/**
 * Basic UI for loading screen, with scope for future scalability.
 */
@Composable
fun LoadingScreen() {
    Text(
        text = stringResource(id = R.string.loading_message),
        fontSize = 16.sp,
        style = MaterialTheme.typography.bodyLarge
    )
}

@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES, name = "DefaultPreviewDark")
@Preview(uiMode = Configuration.UI_MODE_NIGHT_NO, name = "DefaultPreviewLight")
@Composable
fun DefaultLoadingPreview() {
    WeatherAppTheme {
        LoadingScreen()
    }
}
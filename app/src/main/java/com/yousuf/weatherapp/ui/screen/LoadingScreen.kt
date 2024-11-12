package com.yousuf.weatherapp.ui.screen

import android.content.res.Configuration
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
import com.yousuf.weatherapp.R
import com.yousuf.weatherapp.ui.theme.WeatherAppTheme

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

@Preview(
    showBackground = true,
    showSystemUi = true,
    device = "id:pixel_5",
    uiMode = Configuration.UI_MODE_NIGHT_YES,
    name = "Pixel5"
)
@Preview(
    showBackground = true,
    showSystemUi = true,
    device = "id:pixel_6",
    uiMode = Configuration.UI_MODE_NIGHT_NO,
    name = "Pixel6")
@Composable
fun DefaultLoadingPreview() {
    WeatherAppTheme {
        Column (
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            LoadingScreen()
        }
    }
}
package com.yousuf.weatherapp.ui.screen

import android.content.res.Configuration
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.intl.Locale
import androidx.compose.ui.text.toUpperCase
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.yousuf.weatherapp.R
import com.yousuf.weatherapp.WeatherViewModel
import com.yousuf.weatherapp.ui.theme.WeatherAppTheme

@Composable
fun WeatherScreen(
    viewModel: WeatherViewModel = hiltViewModel()
) {

    val weatherData = remember("weatherData") { viewModel.weatherData }

    weatherData.value?.let { weather ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {

            Text(
                text = viewModel.lastCitySearched,
                fontSize = 24.sp,
                style = MaterialTheme.typography.bodyLarge,
            )
            Spacer(modifier = Modifier.height(16.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth(.6f),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    modifier = Modifier
                        .padding(end = 16.dp)
                        .align(Alignment.CenterStart),
                    text = weather.temp,
                    fontSize = 20.sp
                )

                Text(
                    modifier = Modifier
                        .padding(bottom = 20.dp)
                        .align(Alignment.TopEnd),
                    text = weather.tempMax,
                    fontSize = 12.sp
                )
                Text(
                    modifier = Modifier
                        .padding(top = 20.dp)
                        .align(Alignment.BottomEnd),
                    text = weather.tempMin,
                    fontSize = 12.sp
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(.6f),
                horizontalArrangement = Arrangement.Center
            ) {
                Text(
                    text = weather.weather?.toUpperCase(Locale.current)
                        ?: stringResource(R.string.unavailable),
                    fontSize = 18.sp,
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier
                        .align(Alignment.CenterVertically)
                )
                Spacer(modifier = Modifier.width(8.dp))
                AsyncImage(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(weather.iconUrl)
                        .crossfade(true)
                        .build(),
                    contentDescription = "Weather Icon",
                    contentScale = ContentScale.None,
                    modifier = Modifier
                        .clip(CircleShape)
                        .size(56.dp)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Column {
                weather.feelsLike?.let { feelsLike ->
                    Text(
                        text = stringResource(R.string.feels_like, feelsLike),
                        fontSize = 12.sp
                    )
                }

                Spacer(modifier = Modifier.height(4.dp))

                weather.pressure?.let { pressure ->
                    Text(
                        text = stringResource(id = R.string.presure, pressure),
                        fontSize = 12.sp
                    )
                }

                Spacer(modifier = Modifier.height(4.dp))

                weather.humidity?.let { humidity ->
                    Text(
                        text = stringResource(R.string.humidity, humidity),
                        fontSize = 12.sp
                    )
                }
            }
        }
    }
}

@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES, name = "DefaultPreviewDark")
@Preview(uiMode = Configuration.UI_MODE_NIGHT_NO, name = "DefaultPreviewLight")
@Composable
fun DefaultWeatherPreview() {
    WeatherAppTheme {
        WeatherScreen()
    }
}
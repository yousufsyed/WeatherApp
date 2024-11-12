package com.yousuf.weatherapp.ui.screen

import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.yousuf.weatherapp.WeatherUiState
import com.yousuf.weatherapp.WeatherViewModel

@Composable
fun WeatherInfoPrompt(
    modifier: Modifier,
    viewModel: WeatherViewModel
) = Box(
    modifier = modifier,
    contentAlignment = Alignment.Center,
) {
    val weatherModel = viewModel.weatherUiState.collectAsStateWithLifecycle()

    val state by remember { weatherModel }
    when (val result = state) {

        WeatherUiState.Loading -> LoadingScreen()
        is WeatherUiState.Success -> WeatherScreen(weather = result.weather, cityName =viewModel.searchQuery.value)
        is WeatherUiState.Error -> ErrorScreen(result.message, viewModel)
        WeatherUiState.Search -> SearchScreen(viewModel)
    }
}
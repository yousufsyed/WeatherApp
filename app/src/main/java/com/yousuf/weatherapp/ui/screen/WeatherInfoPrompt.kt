package com.yousuf.weatherapp.ui.screen

import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.yousuf.weatherapp.WeatherUiState
import com.yousuf.weatherapp.WeatherViewModel

@Composable
fun WeatherInfoPrompt(
    modifier: Modifier,
    viewModel: WeatherViewModel = hiltViewModel(),
) = Box(
    modifier = modifier,
    contentAlignment = Alignment.Center,
) {
    val weatherModel = viewModel.weatherUiState.collectAsStateWithLifecycle()

    val state by remember { weatherModel }

    when (state) {
        WeatherUiState.Loading -> LoadingScreen()
        WeatherUiState.Success -> WeatherScreen()
        WeatherUiState.Error -> ErrorScreen()
        WeatherUiState.Search -> SearchScreen()
    }
}
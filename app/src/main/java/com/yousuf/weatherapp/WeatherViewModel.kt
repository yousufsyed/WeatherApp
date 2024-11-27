package com.yousuf.weatherapp

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.yousuf.weatherapp.WeatherUiState.Error
import com.yousuf.weatherapp.WeatherUiState.Loading
import com.yousuf.weatherapp.WeatherUiState.Search
import com.yousuf.weatherapp.WeatherUiState.Success
import com.yousuf.weatherapp.network.data.GeoLocation
import com.yousuf.weatherapp.network.data.WeatherData
import com.yousuf.weatherapp.provider.GeoLocationDataSource
import com.yousuf.weatherapp.provider.GeoLocationProvider
import com.yousuf.weatherapp.provider.LocationProvider
import com.yousuf.weatherapp.provider.WeatherProvider
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class WeatherViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
    private val weatherProvider: WeatherProvider,
    private val geoLocationProvider: GeoLocationProvider,
    private val locationProvider: LocationProvider
) : ViewModel() {

    private val _weatherUiState = MutableStateFlow<WeatherUiState>(Search)
    val weatherUiState = _weatherUiState.asStateFlow()

    private val _canRetry = MutableStateFlow(true)
    val canRetry = _canRetry.asStateFlow()

    var errorMessage = mutableStateOf("")
        private set

    var weatherData = mutableStateOf(null as WeatherData?)
        private set

    var searchQuery = locationProvider.locationFlow()
        private set

    var lastCitySearched = ""
        private set

    init {
        fetchLastKnownCity()
    }

    fun updateSearchQuery(city: String) {
        if(city.isNotBlank()) {
            locationProvider.updateLocation(city)
        }
    }

    fun showSearch() {
        _weatherUiState.update { Search }
    }

    // fetch weather data and display appropriate state
    fun getWeatherData(city: String) {
        _canRetry.update { false } // reset retry flag to false
        _weatherUiState.update { Loading }
        lastCitySearched = city
        fetchWeatherData()
    }

    private fun fetchWeatherData() {
        viewModelScope.launch {
            runCatching {
                // save city to prefs
                saveCity(lastCitySearched)
                locationProvider.saveCityToPrefs(lastCitySearched)
                // gets the geo location
                val geoLocation = geoLocationProvider.getGeoLocation(lastCitySearched)
                // get weather data
                weatherProvider.getWeatherData(geoLocation)
            }.onSuccess { weather ->
                weatherData.value = weather
                _weatherUiState.update { Success }
                _canRetry.update { true }
            }.onFailure { error ->
                // TODO need to update error message logic to support localization.
                errorMessage.value = error.message ?: "Unknown error"
                _weatherUiState.update { Error }
                _canRetry.update { true }
            }
            saveCity(lastCitySearched)
        }
    }

    fun retry() {
        _canRetry.update { false } // reset retry flag to false
        _weatherUiState.update { Loading }
        fetchWeatherData()
    }

    // save city to savedStateHandle to persist across process death.
    private fun saveCity(city: String) {
        savedStateHandle["city"] = city
    }

    private fun fetchLastKnownCity() {
        savedStateHandle.get<String>("city")?.let {
            locationProvider.updateLocation(it)
        }
    }
}

sealed class WeatherUiState {
    data object Search : WeatherUiState()
    data object Loading : WeatherUiState()
    data object Error : WeatherUiState()
    data object Success : WeatherUiState()
}
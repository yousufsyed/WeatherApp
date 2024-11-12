package com.yousuf.weatherapp

import android.content.Context
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.yousuf.weatherapp.WeatherUiState.Error
import com.yousuf.weatherapp.network.data.GeoLocation
import com.yousuf.weatherapp.network.data.WeatherData
import com.yousuf.weatherapp.provider.GeoLocationProvider
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
    private val appPrefs: AppPrefs
) : ViewModel() {

    private val _weatherUiState = MutableStateFlow<WeatherUiState>(WeatherUiState.Search)
    val weatherUiState = _weatherUiState.asStateFlow()

    private val _canRetry = MutableStateFlow(true)
    val canRetry = _canRetry.asStateFlow()

    var searchQuery = mutableStateOf(getLastKnownCity())
        private set

    var errorMessage = mutableStateOf("")
        private set

    var weatherData = mutableStateOf(null as WeatherData?)
        private set

    var locationErrorMessage = mutableStateOf(false)
        private set

    var showDialog = mutableStateOf(true)
        private set

    fun disablePermissionsDialog() {
        showDialog.value = false
    }

    init {
        viewModelScope.launch {
            fetchLocationPrefs()
        }
    }

    fun updateSearchQuery(query: String) {
        searchQuery.value = query
        saveCity(query)
    }

    fun showSearch() {
        _weatherUiState.update { WeatherUiState.Search }
    }

    // fetch weather data and display appropriate state
    fun getWeatherData() {
        if (searchQuery.value.isEmpty()) {
            showSearch()
            return
        }

        _canRetry.update { false } // reset retry flag to false
        _weatherUiState.update { WeatherUiState.Loading }
        fetchWeatherData(searchQuery.value)
    }

    private fun fetchWeatherData(city: String) {
        viewModelScope.launch {
            runCatching {
                // gets the geo location
                val geoLocation = geoLocationProvider.getGeoLocation(city)
                // get weather data
                weatherProvider.getWeatherData(geoLocation)
            }.onSuccess { weather ->
                weatherData.value = weather
                _weatherUiState.update { WeatherUiState.Success }
                _canRetry.update { true }
                saveCityToPrefs(city)
            }.onFailure { error ->
                // TODO need to update error message logic to support localization.
                errorMessage.value = error.message ?: "Unknown error"
                _weatherUiState.update { Error }
                _canRetry.update { true }
            }
        }
    }

    fun retry() {
        getLastKnownCity().let {
            getWeatherData()
        }
    }

    // update location fetch from device
    fun updateLocation(location: GeoLocation) {
        updateSearchQuery(location.city)
        geoLocationProvider.updateGeoLocation(location)
    }

    private suspend fun fetchLocationPrefs() {
        appPrefs.cityFlow.collect { city ->
            updateSearchQuery(city)
        }
    }

    private suspend fun saveCityToPrefs(city: String) {
        appPrefs.updateShowCompleted(city)
    }

    // save city to savedStateHandle to persist across process death.
    private fun saveCity(city: String) {
        savedStateHandle["city"] = city
    }

    private fun getLastKnownCity(): String {
        return savedStateHandle.get<String>("city").orEmpty()
    }
}

sealed class WeatherUiState {
    data object Search : WeatherUiState()
    data object Loading : WeatherUiState()
    data object Error : WeatherUiState()
    data object Success : WeatherUiState()
}
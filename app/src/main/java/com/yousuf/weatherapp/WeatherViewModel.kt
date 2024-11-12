package com.yousuf.weatherapp

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.AbstractSavedStateViewModelFactory
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.yousuf.weatherapp.WeatherUiState.Error
import com.yousuf.weatherapp.WeatherUiState.Loading
import com.yousuf.weatherapp.WeatherUiState.Search
import com.yousuf.weatherapp.WeatherUiState.Success
import com.yousuf.weatherapp.network.data.GeoLocation
import com.yousuf.weatherapp.network.data.WeatherData
import com.yousuf.weatherapp.provider.GeoLocationProvider
import com.yousuf.weatherapp.provider.WeatherProvider
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

class WeatherViewModel(
    private val savedStateHandle: SavedStateHandle,
    private val weatherProvider: WeatherProvider,
    private val geoLocationProvider: GeoLocationProvider,
    private val appPrefs: AppPrefs
) : ViewModel() {

    private val _weatherUiState = MutableStateFlow<WeatherUiState>(Search)
    val weatherUiState = _weatherUiState.asStateFlow()

    private val _canRetry = MutableStateFlow(true)
    val canRetry = _canRetry.asStateFlow()

    var searchQuery = mutableStateOf(getLastKnownCity())
        private set

    init {
        fetchLocationPrefs()
    }

    fun updateLocation(location: GeoLocation) {
        updateSearchQuery(location.city)
        geoLocationProvider.updateGeoLocation(location)
    }

    // fetch weather data and display appropriate state
    fun getWeatherData() {
        if (searchQuery.value.isEmpty()) {
            showSearch()
            return
        }

        _canRetry.update { false } // reset retry flag to false
        _weatherUiState.update { Loading }
        fetchWeatherData(searchQuery.value)
    }

    private fun fetchWeatherData(city: String) {
        viewModelScope.launch {
            runCatching {
                // gets the geo location
                val geoLocation = geoLocationProvider.getGeoLocation(city)
                // get weather data
                weatherProvider.getWeatherData(geoLocation)
            }.onSuccess { weatherData ->
                _weatherUiState.update { Success(weatherData) }
                _canRetry.update { true }
                saveCityToPrefs(city)
            }.onFailure { error ->
                // TODO need to update error message logic to support localization.
                _weatherUiState.update { Error(error.message ?: "Unknown error") }
                _canRetry.update { true }
            }
        }
    }

    fun updateSearchQuery(query: String) {
        searchQuery.value = query
        saveCity(query)
    }

    fun showSearch() {
        _weatherUiState.update { Search }
    }

    fun retry() {
        getLastKnownCity().let {
            getWeatherData()
        }
    }

    // save city to savedStateHandle to persist across process death.
    private fun saveCity(city: String) {
        savedStateHandle["city"] = city
    }

    private fun getLastKnownCity(): String {
        return savedStateHandle.get<String>("city").orEmpty()
    }

    private fun fetchLocationPrefs() {
        // TODO: This might conflict with the onChange callback,
        //  need to find the right way to have single source of truth to avoid ambiguity.
        viewModelScope.launch {
            appPrefs.cityFlow.collect { city ->
                updateSearchQuery(city)
            }
        }
    }

    private fun saveCityToPrefs(city: String) {
        viewModelScope.launch {
            appPrefs.updateShowCompleted(city)
        }
    }
}

/**
 * Factory class to instantiate [WeatherViewModel]
 */
class WeatherViewModelFactory @Inject constructor(
    private val weatherProvider: WeatherProvider,
    private val geoLocationProvider: GeoLocationProvider,
    private val appPrefs: AppPrefs
) : AbstractSavedStateViewModelFactory() {
    override fun <T : ViewModel> create(
        key: String,
        modelClass: Class<T>,
        handle: SavedStateHandle
    ): T {
        if (modelClass.isAssignableFrom(WeatherViewModel::class.java)) {
            return WeatherViewModel(handle, weatherProvider, geoLocationProvider, appPrefs) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }

}

sealed class WeatherUiState {
    data object Search : WeatherUiState()
    data object Loading : WeatherUiState()
    data class Success(val weather: WeatherData) : WeatherUiState()
    data class Error(val message: String) : WeatherUiState()
}
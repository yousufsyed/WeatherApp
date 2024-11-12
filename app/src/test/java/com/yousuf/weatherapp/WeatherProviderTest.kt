package com.yousuf.weatherapp

import com.yousuf.weatherapp.network.WeatherClient
import com.yousuf.weatherapp.network.WeatherDataException
import com.yousuf.weatherapp.provider.DispatcherProvider
import com.yousuf.weatherapp.provider.WeatherProvider
import com.yousuf.weatherapp.provider.WeatherProviderImpl
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test

class WeatherProviderTest {

    @OptIn(ExperimentalCoroutinesApi::class)
    private val dispatcher = UnconfinedTestDispatcher()
    private val dispatcherProvider = DispatcherProvider(io = dispatcher)

    private val weatherClient = mockk<WeatherClient>()
    private lateinit var weatherProvider: WeatherProvider

    @Before
    fun setUp() {
        weatherProvider = WeatherProviderImpl(
            weatherClient = weatherClient,
            dispatcherProvider = dispatcherProvider
        )
    }

    @Test
    fun `test successful weather data fetch`() {
        coEvery { weatherClient.fetchWeatherData(geoLocation) } returns weatherData

        runTest {
            var result = weatherProvider.getWeatherData(geoLocation)
            assert(result == weatherData)

            result = weatherProvider.getWeatherData(geoLocation)
            assert(result == weatherData)

            coVerify(exactly = 1) { weatherClient.fetchWeatherData(geoLocation) }
        }
    }

    @Test(expected = WeatherDataException::class)
    fun `test weather data fetch failure`() {
        coEvery { weatherClient.fetchWeatherData(geoLocation) } throws WeatherDataException()

        runTest {
            weatherProvider.getWeatherData(geoLocation)
            coVerify(exactly = 1) { weatherClient.fetchWeatherData(geoLocation) }
        }
    }
}
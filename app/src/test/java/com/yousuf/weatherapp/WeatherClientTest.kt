package com.yousuf.weatherapp

import com.yousuf.weatherapp.network.WeatherClientImpl
import com.yousuf.weatherapp.network.WeatherDataException
import com.yousuf.weatherapp.network.WeatherService
import com.yousuf.weatherapp.network.data.GeoLocation
import com.yousuf.weatherapp.network.data.WeatherData
import com.yousuf.weatherapp.provider.DispatcherProvider
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.After
import org.junit.Assert
import org.junit.Before
import org.junit.Ignore
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import retrofit2.Retrofit

@RunWith(JUnit4::class)
class WeatherClientTest {

    private val mockWebServer = MockWebServer()
    private lateinit var weatherService: WeatherService

    @OptIn(ExperimentalCoroutinesApi::class)
    private val dispatcher = UnconfinedTestDispatcher()
    private val dispatcherProvider = DispatcherProvider(io = dispatcher)

    private lateinit var weatherClient: WeatherClientImpl

    @Before
    fun setUp() {
        mockWebServer.start()
        weatherService = Retrofit.Builder()
            .baseUrl(mockWebServer.url("/"))
            .build().create(WeatherService::class.java)

        weatherClient = WeatherClientImpl(
            dispatcherProvider,
            weatherService = weatherService
        )
    }

    @After
    fun tearDown() {
        mockWebServer.shutdown()
    }

    @Test
    @Ignore("TODO; need to see why mock response is not working")
    fun testFetchWeatherDataSuccess() {
        runBlocking {
            val response = MockResponse()
                .setResponseCode(200)
                .setBody(weatherResponse.trimIndent())

            mockWebServer.enqueue(response)

            val result = weatherClient.fetchWeatherData(geoLocation)
            Assert.assertEquals(result, weatherData)
        }
    }

    @Test(expected = WeatherDataException::class)
    fun testFetchWeatherDataFailure() {
        runBlocking {
            MockResponse().setResponseCode(404).also {
                mockWebServer.enqueue(it)
            }

            weatherClient.fetchWeatherData(geoLocation)
        }
    }

}

private val weatherResponse = """ 
    {
       "coord": {
          "lon": 7.367,
          "lat": 45.133
       },
       "weather": [
          {
             "id": 501,
             "main": "Rain",
             "description": "moderate rain",
             "icon": "10d"
          }
       ],
       "base": "stations",
       "main": {
          "temp": 284.2,
          "feels_like": 282.93,
          "temp_min": 283.06,
          "temp_max": 286.82,
          "pressure": 1021,
          "humidity": 60,
          "sea_level": 1021,
          "grnd_level": 910
       },
       "visibility": 10000,
       "wind": {
          "speed": 4.09,
          "deg": 121,
          "gust": 3.47
       },
       "rain": {
          "1h": 2.73
       },
       "clouds": {
          "all": 83
       },
       "dt": 1726660758,
       "sys": {
          "type": 1,
          "id": 6736,
          "country": "IT",
          "sunrise": 1726636384,
          "sunset": 1726680975
       },
       "timezone": 7200,
       "id": 3165523,
       "name": "Province of Turin",
       "cod": 200
    }     
""".trimIndent()
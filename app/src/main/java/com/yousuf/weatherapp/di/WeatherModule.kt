package com.yousuf.weatherapp.di

import android.content.Context
import com.yousuf.weatherapp.AppPrefs
import com.yousuf.weatherapp.network.DefaultGeoLocationClient
import com.yousuf.weatherapp.network.DefaultWeatherClient
import com.yousuf.weatherapp.network.GeoLocationClient
import com.yousuf.weatherapp.network.WeatherClient
import com.yousuf.weatherapp.network.WeatherService
import com.yousuf.weatherapp.provider.DefaultGeoLocationDataSource
import com.yousuf.weatherapp.provider.DefaultLocationProvider
import com.yousuf.weatherapp.provider.DispatcherProvider
import com.yousuf.weatherapp.provider.GeoLocationDataSource
import com.yousuf.weatherapp.provider.GeoLocationProvider
import com.yousuf.weatherapp.provider.GeoLocationProviderImpl
import com.yousuf.weatherapp.provider.LocationProvider
import com.yousuf.weatherapp.provider.WeatherProvider
import com.yousuf.weatherapp.provider.WeatherProviderImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.Dispatchers
import retrofit2.Retrofit
import javax.inject.Singleton

const val BASE_URL = "https://api.openweathermap.org"

@Module
@InstallIn(SingletonComponent::class)
class WeatherModule {

    @Provides
    fun providesRetrofit(): Retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .build()

    @Provides
    @Singleton
    fun providesWeatherService(retrofit: Retrofit): WeatherService =
        retrofit.create(WeatherService::class.java)

    @Provides
    fun providesWeatherClient(impl: DefaultWeatherClient): WeatherClient = impl

    @Provides
    fun providesWeatherProvider(impl: WeatherProviderImpl): WeatherProvider = impl

    @Provides
    fun providesGeoLocationClient(impl: DefaultGeoLocationClient): GeoLocationClient = impl

    @Provides
    fun providesGeoLocationProvider(impl: GeoLocationProviderImpl): GeoLocationProvider = impl

    @Provides
    fun providesGeoLocationDataSource(impl: DefaultGeoLocationDataSource): GeoLocationDataSource = impl

    @Provides
    @Singleton
    fun providesLocationProvider(impl: DefaultLocationProvider): LocationProvider = impl

    @Provides
    fun providesAppPrefs(
        @ApplicationContext context: Context,
        dispatcherProvider: DispatcherProvider
    ) = AppPrefs(context, dispatcherProvider)

    @Provides
    fun providesDispatchers() = DispatcherProvider(
        io = Dispatchers.IO,
        default = Dispatchers.Default,
        main = Dispatchers.Main
    )
}
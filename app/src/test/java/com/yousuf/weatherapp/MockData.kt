package com.yousuf.weatherapp

import com.yousuf.weatherapp.network.data.GeoLocation
import com.yousuf.weatherapp.network.data.WeatherData

val geoLocation = GeoLocation(
    lat = "45.133",
    lon = "7.367",
    city = "Province of Turin",
    state = "",
    country = "IT"
)

val weatherData = WeatherData(
    temp = "284.2",
    feelsLike = "282.93",
    tempMin = "283.06",
    tempMax = "286.82",
    pressure = 1021,
    humidity = 60,
    description = "moderate rain",
    weather = "rain",
    icon = "10d",
)
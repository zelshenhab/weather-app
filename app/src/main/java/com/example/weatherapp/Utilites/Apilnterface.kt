package com.example.weatherapp.Utilites

import com.example.weatherapp.Models.WeatherModel
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface Apilnterface {

    @GET("weather")
   fun getCurrentWeatherData(
       @Query("lat") lat:String,
       @Query("lon") lon:String,
       @Query("APPID") appid:String
       ) :Call<WeatherModel>

    @GET("weather")
    fun getCityWeatherData(
        @Query("q") q:String,
        @Query("APPID") appid:String
    ):Call<WeatherModel>





}
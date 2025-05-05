package com.aranyalma2.simpleweather.ui.home

import androidx.lifecycle.ViewModel
import com.aranyalma2.simpleweather.data.remote.LocationApiService
import com.aranyalma2.simpleweather.data.remote.WeatherApiService
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val weatherApi: WeatherApiService,
    private val locationApi: LocationApiService
) : ViewModel() { }
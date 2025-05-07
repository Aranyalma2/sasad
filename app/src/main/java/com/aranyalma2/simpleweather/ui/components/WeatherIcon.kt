package com.aranyalma2.simpleweather.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.painterResource
import com.aranyalma2.simpleweather.R
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.ui.graphics.Color

@Composable
fun WeatherIcon(
    weatherCode: Int,
    modifier: Modifier = Modifier,
    isNight: Boolean = false
) {
    val iconResId = when (weatherCode) {
        0 -> if (isNight) R.drawable.clear_night else R.drawable.sunny
        1, 2, 3 -> if (isNight) R.drawable.cloudy_night else R.drawable.cloudy
        45, 48 -> R.drawable.fog
        51, 53, 55, 56, 57 -> R.drawable.rainy
        61, 63, 65, 66, 67, 80, 81, 82 -> R.drawable.pouring
        71, 73, 75, 77, 85, 86 -> R.drawable.snowy
        95, 96, 99 -> R.drawable.storm
        else -> R.drawable.sunny // Default
    }

    Image(
        painter = painterResource(id = iconResId),
        contentDescription = "Weather icon",
        modifier = modifier
    )
}

@Composable
fun WeatherIconBlurred(
    weatherCode: Int,
    modifier: Modifier = Modifier,
    isNight: Boolean = false
) {
    val iconResId = when (weatherCode) {
        0 -> if (isNight) R.drawable.clear_night else R.drawable.sunny
        1, 2, 3 -> if (isNight) R.drawable.cloudy_night else R.drawable.cloudy
        45, 48 -> R.drawable.fog
        51, 53, 55, 56, 57 -> R.drawable.rainy
        61, 63, 65, 66, 67, 80, 81, 82 -> R.drawable.pouring
        71, 73, 75, 77, 85, 86 -> R.drawable.snowy
        95, 96, 99 -> R.drawable.storm
        else -> R.drawable.sunny
    }

    Box(modifier = modifier) {
        // Background image with low alpha (simulated blur)
        Image(
            painter = painterResource(id = iconResId),
            contentDescription = "Blurred weather icon",
            modifier = Modifier
                .fillMaxSize()
                .graphicsLayer(alpha = 0.8f)
        )

        // Optional semi-transparent overlay for depth
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.05f))
        )
    }
}

package com.aranyalma2.simpleweather.ui.components

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.aranyalma2.simpleweather.R
import java.time.LocalTime

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

    // Use Modifier.fillMaxWidth() to fill the available width
    Image(
        painter = painterResource(id = iconResId),
        contentDescription = "Weather icon",
        modifier = modifier.fillMaxWidth() // Make the icon fill the width
    )
}

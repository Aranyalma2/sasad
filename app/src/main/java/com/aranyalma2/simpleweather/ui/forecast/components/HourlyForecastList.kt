package com.aranyalma2.simpleweather.ui.forecast.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.aranyalma2.simpleweather.domain.model.HourlyWeather
import com.aranyalma2.simpleweather.ui.components.WeatherIcon
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@Composable
fun HourlyForecastList(hourlyData: List<HourlyWeather>) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
        ) {
            Text(
                text = "Hourly Forecast",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                contentPadding = PaddingValues(horizontal = 8.dp)
            ) {
                items(hourlyData) { hourlyWeather ->
                    HourlyWeatherItem(hourlyWeather = hourlyWeather)
                }
            }
        }
    }
}

@Composable
fun HourlyWeatherItem(hourlyWeather: HourlyWeather) {
    val dateTime = LocalDateTime.parse(hourlyWeather.time, DateTimeFormatter.ISO_DATE_TIME)
    val formattedTime = dateTime.format(DateTimeFormatter.ofPattern("HH:mm"))

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.width(70.dp)
    ) {
        Text(
            text = formattedTime,
            style = MaterialTheme.typography.bodyMedium,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(8.dp))

        WeatherIcon(
            weatherCode = hourlyWeather.weatherCode
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "${hourlyWeather.temperature.toInt()}°",
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = "${hourlyWeather.precipitationProbability}%",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
            textAlign = TextAlign.Center
        )
    }
}
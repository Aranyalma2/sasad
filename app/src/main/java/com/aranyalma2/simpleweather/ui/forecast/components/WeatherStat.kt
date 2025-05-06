package com.aranyalma2.simpleweather.ui.forecast.components

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

@Composable
fun WeatherStat(
    feelsLike: String,
    humidity: String,
    windDirection: String,
    windSpeed: String,
    precipitationPrediction: String,
    precipitation: String
) {
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
                text = "Weather Details",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            Row(modifier = Modifier.fillMaxWidth()) {
                // First column of stats
                Column(
                    modifier = Modifier.weight(1f),
                    horizontalAlignment = Alignment.Start
                ) {
                    WeatherStatItem(
                        icon = Icons.Default.Thermostat,
                        title = "Feels Like",
                        value = feelsLike
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    WeatherStatItem(
                        icon = Icons.Default.WaterDrop,
                        title = "Humidity",
                        value = humidity
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    WeatherStatItem(
                        icon = Icons.Default.Navigation,
                        title = "Wind Direction",
                        value = windDirection
                    )
                }

                // Second column of stats
                Column(
                    modifier = Modifier.weight(1f),
                    horizontalAlignment = Alignment.Start
                ) {
                    WeatherStatItem(
                        icon = Icons.Default.Air,
                        title = "Wind Speed",
                        value = windSpeed
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    WeatherStatItem(
                        icon = Icons.Default.Umbrella,
                        title = "Rain Chance",
                        value = precipitationPrediction
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    WeatherStatItem(
                        icon = Icons.Default.Opacity,
                        title = "Precipitation",
                        value = precipitation
                    )
                }
            }
        }
    }
}

@Composable
fun WeatherStatItem(
    icon: ImageVector,
    title: String,
    value: String
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Start
    ) {
        Icon(
            imageVector = icon,
            contentDescription = title,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(24.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Column {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
            )
            Text(
                text = value,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}
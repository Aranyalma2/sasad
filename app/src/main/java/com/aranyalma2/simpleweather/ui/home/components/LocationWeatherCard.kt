package com.aranyalma2.simpleweather.ui.home.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.aranyalma2.simpleweather.ui.components.WeatherIcon
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import kotlin.time.TimeSource

@Composable
fun LocationWeatherCard(
    cityName: String,
    country: String,
    weatherCode: Int,
    isFavorite: Boolean,
    onFavoriteClick: () -> Unit,
    onCardClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp) // Added margin between cards
            .clickable { onCardClick() },
        shape = MaterialTheme.shapes.small, // Rounded edges
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(0.dp), // Zero margin inside the card
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Weather Icon: Full width, height proportional to aspect ratio
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp) // Height for the weather icon, adjust based on design needs
            ) {
                // Weather icon filling the width of the box
                val currentTime = LocalDateTime.now()
                val isNight = currentTime.hour < 6 || currentTime.hour >= 20
                WeatherIcon(
                    weatherCode = weatherCode,
                    modifier = Modifier.fillMaxWidth(),
                    isNight = isNight
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Information under the picture: City name, country, and favorite icon
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // City Name and Country
                Column(
                    horizontalAlignment = Alignment.Start
                ) {
                    // City Name
                    Text(
                        text = cityName,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                    // Country Name
                    Text(
                        text = country,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                    )
                }

                // Favorite Icon
                IconButton(onClick = onFavoriteClick) {
                    Icon(
                        imageVector = if (isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                        contentDescription = if (isFavorite) "Remove from favorites" else "Add to favorites",
                        tint = if (isFavorite) Color.Red else MaterialTheme.colorScheme.onSurface
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}




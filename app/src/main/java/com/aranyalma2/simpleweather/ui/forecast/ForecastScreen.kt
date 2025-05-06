package com.aranyalma2.simpleweather.ui.forecast

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.aranyalma2.simpleweather.ui.components.WeatherIcon
import com.aranyalma2.simpleweather.ui.forecast.components.DailyForecastCard
import com.aranyalma2.simpleweather.ui.forecast.components.HourlyForecastList
import com.aranyalma2.simpleweather.ui.forecast.components.WeatherStat
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ForecastScreen(
    locationId: Int,
    onBackPressed: () -> Unit,
    viewModel: ForecastViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(uiState.locationName) },
                navigationIcon = {
                    IconButton(onClick = onBackPressed) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },
                actions = {
                    IconButton(onClick = { viewModel.refreshWeatherData(locationId) }) {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = "Refresh"
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            if (uiState.isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center)
                )
            } else if (uiState.error != null) {
                ErrorMessage(
                    errorMessage = uiState.error!!,
                    modifier = Modifier.align(Alignment.Center)
                )
            } else {
                // Weather content
                ForecastContent(uiState)
            }
        }
    }
}

@Composable
fun ForecastContent(uiState: ForecastUiState) {
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
    ) {
        // Current weather header
        CurrentWeatherHeader(uiState)

        // Weather statistics
        uiState.currentWeather?.let { currentWeather ->
            WeatherStat(
                feelsLike = "${currentWeather.apparentTemperature.toInt()}°C",
                humidity = "${currentWeather.humidity}%",
                windDirection = getWindDirectionText(currentWeather.windDirection),
                windSpeed = "${currentWeather.windSpeed} km/h",
                precipitationPrediction = "${currentWeather.precipitationProbability}%",
                precipitation = "${currentWeather.precipitation} mm"
            )
        }

        // Hourly forecast
        if (uiState.hourlyWeather.isNotEmpty()) {
            HourlyForecastList(hourlyData = uiState.hourlyWeather)
        }

        // Daily forecast
        if (uiState.dailyWeather.isNotEmpty()) {
            DailyForecastCard(dailyWeather = uiState.dailyWeather)
        }

        // Add some space at the bottom
        Spacer(modifier = Modifier.height(16.dp))
    }
}

@Composable
fun CurrentWeatherHeader(uiState: ForecastUiState) {
    uiState.currentWeather?.let { currentWeather ->
        val dateTime = LocalDateTime.parse(currentWeather.time, DateTimeFormatter.ISO_DATE_TIME)
        val formattedDate = dateTime.format(DateTimeFormatter.ofPattern("EEEE, MMM d"))

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "${uiState.locationName}, ${uiState.country}",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )

            Text(
                text = formattedDate,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
            )

            Spacer(modifier = Modifier.height(16.dp))

            val dateTime = LocalDateTime.parse(currentWeather.time, DateTimeFormatter.ISO_DATE_TIME)
            val isNight = dateTime.hour < 6 || dateTime.hour >= 20

            WeatherIcon(
                weatherCode = currentWeather.weatherCode,
                isNight = isNight
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "${currentWeather.temperature.toInt()}°C",
                fontSize = 48.sp,
                fontWeight = FontWeight.Bold
            )

            Text(
                text = getWeatherDescription(currentWeather.weatherCode),
                style = MaterialTheme.typography.bodyLarge
            )
        }
    }
}

@Composable
fun ErrorMessage(
    errorMessage: String,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Oops!",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = errorMessage,
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center
        )
    }
}

fun getWindDirectionText(degrees: Int): String {
    return when {
        degrees >= 337.5 || degrees < 22.5 -> "N"
        degrees >= 22.5 && degrees < 67.5 -> "NE"
        degrees >= 67.5 && degrees < 112.5 -> "E"
        degrees >= 112.5 && degrees < 157.5 -> "SE"
        degrees >= 157.5 && degrees < 202.5 -> "S"
        degrees >= 202.5 && degrees < 247.5 -> "SW"
        degrees >= 247.5 && degrees < 292.5 -> "W"
        else -> "NW"
    }
}

fun getWeatherDescription(weatherCode: Int): String {
    return when (weatherCode) {
        0 -> "Clear Sky"
        1 -> "Mainly Clear"
        2 -> "Partly Cloudy"
        3 -> "Overcast"
        45, 48 -> "Fog"
        51, 53, 55 -> "Drizzle"
        56, 57 -> "Freezing Drizzle"
        61, 63, 65 -> "Rain"
        66, 67 -> "Freezing Rain"
        71, 73, 75 -> "Snow"
        77 -> "Snow Grains"
        80, 81, 82 -> "Rain Showers"
        85, 86 -> "Snow Showers"
        95 -> "Thunderstorm"
        96, 99 -> "Thunderstorm with Hail"
        else -> "Unknown"
    }
}
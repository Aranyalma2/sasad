package com.aranyalma2.simpleweather.ui.home

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.aranyalma2.simpleweather.ui.home.components.LocationWeatherCard
import com.aranyalma2.simpleweather.util.PermissionHandler

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onNavigateToSearch: () -> Unit,
    onNavigateToForecast: (Int) -> Unit,
    viewModel: HomeViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    // State for the confirmation dialog
    var showDeleteConfirmation by remember { mutableStateOf(false) }
    var locationToDelete by remember { mutableStateOf<Int?>(null) }
    var locationNameToDelete by remember { mutableStateOf("") }

    // Request location permission when the screen is first shown
    PermissionHandler.RequestLocationPermission(
        onPermissionGranted = {
            // Permission granted, fetch current location weather
            viewModel.refreshWeatherData()
        }
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Simple Weather") },
                actions = {
                    IconButton(onClick = { viewModel.refreshWeatherData() }) {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = "Refresh"
                        )
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onNavigateToSearch,
                containerColor = MaterialTheme.colorScheme.primary
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Add Location"
                )
            }
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
            } else if (uiState.locations.isEmpty()) {
                EmptyStateMessage(
                    modifier = Modifier.align(Alignment.Center),
                    onAddLocationClick = onNavigateToSearch
                )
            } else {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(vertical = 8.dp)
                ) {
                    itemsIndexed(uiState.locations) { _, locationWithWeather ->
                        val location = locationWithWeather.location
                        val weatherCode = locationWithWeather.hourly.firstOrNull()?.weatherCode ?: 0
                        val isCurrentLocation = location.id == HomeViewModel.CURRENT_LOCATION_ID

                        LocationWeatherCard(
                            cityName = location.name,
                            country = location.country,
                            weatherCode = weatherCode,
                            isFavorite = true,
                            isCurrentLocation = isCurrentLocation,
                            onFavoriteClick = {
                                // Show confirmation dialog instead of immediate deletion
                                locationToDelete = location.id
                                locationNameToDelete = location.name
                                showDeleteConfirmation = true
                            },
                            onCardClick = { onNavigateToForecast(location.id) }
                        )
                    }
                }
            }

            uiState.error?.let { error ->
                Snackbar(
                    modifier = Modifier
                        .padding(16.dp)
                        .align(Alignment.BottomCenter)
                ) {
                    Text(error)
                }
            }

            // Delete confirmation dialog
            if (showDeleteConfirmation && locationToDelete != null) {
                AlertDialog(
                    onDismissRequest = {
                        showDeleteConfirmation = false
                        locationToDelete = null
                    },
                    title = { Text("Remove Location") },
                    text = { Text("Are you sure you want to remove $locationNameToDelete from your favorites?") },
                    confirmButton = {
                        TextButton(onClick = {
                            locationToDelete?.let { viewModel.toggleFavorite(it) }
                            showDeleteConfirmation = false
                            locationToDelete = null
                        }) {
                            Text("Remove")
                        }
                    },
                    dismissButton = {
                        TextButton(onClick = {
                            showDeleteConfirmation = false
                            locationToDelete = null
                        }) {
                            Text("Cancel")
                        }
                    }
                )
            }
        }
    }
}

@Composable
private fun EmptyStateMessage(
    modifier: Modifier = Modifier,
    onAddLocationClick: () -> Unit
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "No locations added yet",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Add a location to see weather forecasts",
            style = MaterialTheme.typography.bodyMedium
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(onClick = onAddLocationClick) {
            Text("Add Location")
        }
    }
}
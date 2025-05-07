package com.aranyalma2.simpleweather.util

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

/**
 * A utility class to handle requesting location permissions in Compose.
 */
object PermissionHandler {
    /**
     * Check if the app has location permissions
     */
    fun hasLocationPermission(context: Context): Boolean {
        return ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(
                    context,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ) == PackageManager.PERMISSION_GRANTED
    }

    /**
     * A composable function to handle requesting location permissions
     */
    @Composable
    fun RequestLocationPermission(
        rationaleTitle: String = "Location Permission",
        rationaleText: String = "Location permission is needed to show weather for your current location.",
        onPermissionGranted: () -> Unit,
        onPermissionDenied: () -> Unit = {}
    ) {
        val context = LocalContext.current
        var showRationaleDialog by remember { mutableStateOf(false) }

        // Permission launcher
        val locationPermissionLauncher = rememberLauncherForActivityResult(
            contract = ActivityResultContracts.RequestMultiplePermissions()
        ) { permissions ->
            val locationGranted = permissions.entries.any {
                it.key == Manifest.permission.ACCESS_COARSE_LOCATION && it.value ||
                        it.key == Manifest.permission.ACCESS_FINE_LOCATION && it.value
            }

            if (locationGranted) {
                onPermissionGranted()
            } else {
                onPermissionDenied()
            }
        }

        // Check if rationale should be shown
        val shouldShowRationale = ActivityCompat.shouldShowRequestPermissionRationale(
            context as Activity,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) || ActivityCompat.shouldShowRequestPermissionRationale(
            context,
            Manifest.permission.ACCESS_COARSE_LOCATION
        )

        LaunchedEffect(Unit) {
            when {
                hasLocationPermission(context) -> {
                    // Already have permission
                    onPermissionGranted()
                }
                shouldShowRationale -> {
                    // Show dialog explaining why we need permission
                    showRationaleDialog = true
                }
                else -> {
                    // Request permission directly
                    locationPermissionLauncher.launch(
                        arrayOf(
                            Manifest.permission.ACCESS_COARSE_LOCATION,
                            Manifest.permission.ACCESS_FINE_LOCATION
                        )
                    )
                }
            }
        }

        // Rationale dialog
        if (showRationaleDialog) {
            AlertDialog(
                onDismissRequest = {
                    showRationaleDialog = false
                    onPermissionDenied()
                },
                title = { Text(rationaleTitle) },
                text = { Text(rationaleText) },
                confirmButton = {
                    TextButton(onClick = {
                        showRationaleDialog = false
                        locationPermissionLauncher.launch(
                            arrayOf(
                                Manifest.permission.ACCESS_COARSE_LOCATION,
                                Manifest.permission.ACCESS_FINE_LOCATION
                            )
                        )
                    }) {
                        Text("Grant Permission")
                    }
                },
                dismissButton = {
                    TextButton(onClick = {
                        showRationaleDialog = false
                        onPermissionDenied()
                    }) {
                        Text("Cancel")
                    }
                }
            )
        }
    }
}
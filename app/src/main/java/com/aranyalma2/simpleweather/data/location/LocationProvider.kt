package com.aranyalma2.simpleweather.data.location

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.os.Looper
import androidx.core.content.ContextCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.suspendCancellableCoroutine
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.coroutines.resume

@Singleton
class LocationProvider @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val fusedLocationClient: FusedLocationProviderClient =
        LocationServices.getFusedLocationProviderClient(context)

    /**
     * Checks if the app has location permissions
     */
    fun hasLocationPermission(): Boolean {
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
     * Returns the last known location if available, otherwise returns null
     */
    suspend fun getCurrentLocation(): Location? {
        if (!hasLocationPermission()) {
            return null
        }

        return try {
            // First try to get the last known location
            val lastLocationTask = suspendCancellableCoroutine { continuation ->
                fusedLocationClient.lastLocation
                    .addOnSuccessListener { location ->
                        if (continuation.isActive) {
                            continuation.resume(location)
                        }
                    }
                    .addOnFailureListener {
                        if (continuation.isActive) {
                            continuation.resume(null)
                        }
                    }
            }

            // If last location is available, use it
            if (lastLocationTask != null) {
                return lastLocationTask
            }

            // Otherwise request a new location update
            requestLocationUpdate()
        } catch (e: SecurityException) {
            null
        }
    }

    /**
     * Request a single location update
     */
    private suspend fun requestLocationUpdate(): Location? = suspendCancellableCoroutine { continuation ->
        if (!hasLocationPermission()) {
            continuation.resume(null)
            return@suspendCancellableCoroutine
        }

        try {
            val locationRequest = LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 10000)
                .setWaitForAccurateLocation(false)
                .setMinUpdateIntervalMillis(5000)
                .setMaxUpdateDelayMillis(15000)
                .build()

            val locationCallback = object : LocationCallback() {
                override fun onLocationResult(result: LocationResult) {
                    val location = result.lastLocation
                    if (continuation.isActive) {
                        continuation.resume(location)
                    }
                    fusedLocationClient.removeLocationUpdates(this)
                }
            }

            fusedLocationClient.requestLocationUpdates(
                locationRequest,
                locationCallback,
                Looper.getMainLooper()
            )

            // Ensure we clean up when coroutine is cancelled
            continuation.invokeOnCancellation {
                fusedLocationClient.removeLocationUpdates(locationCallback)
            }
        } catch (e: SecurityException) {
            continuation.resume(null)
        }
    }
}
package com.example.android_ota_file_sync.ui.util

import android.Manifest
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue

@Composable
fun rememberMediaPermissionsGranted(): Boolean {
    // State to hold if all requested permissions are granted.
    // Initialize to false, assuming permissions are not granted yet.
    var allPermissionsGranted by remember { mutableStateOf(false) }

    // Determine the list of permissions needed based on Android version
    val permissionsToRequest = remember {
        mutableListOf<String>().apply {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                add(Manifest.permission.READ_MEDIA_IMAGES)
                add(Manifest.permission.READ_MEDIA_VIDEO)
                // Add Manifest.permission.READ_MEDIA_AUDIO if you need it
            } else {
                add(Manifest.permission.READ_EXTERNAL_STORAGE)
            }
        }.toTypedArray() // Convert to Array for the launcher
    }

    // rememberLauncherForActivityResult for multiple permissions
    val permissionsLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permissionsMap ->
        // Check if all requested permissions were granted
        // The 'permissionsMap' contains the permission string as key and a boolean (granted) as value
        allPermissionsGranted = permissionsMap.values.all { it }
    }

    // LaunchedEffect to request permissions when the Composable enters the Composition
    // and when the set of permissionsToRequest changes (though it won't in this specific setup after initial composition)
    LaunchedEffect(permissionsToRequest) {
        // Only launch if there are permissions to request and they haven't been granted yet.
        // You might want to add a check here if permissions are already granted
        // using ContextCompat.checkSelfPermission to avoid re-requesting.
        // For simplicity, this example always launches if allPermissionsGranted is false.
        if (!allPermissionsGranted && permissionsToRequest.isNotEmpty()) {
            permissionsLauncher.launch(permissionsToRequest)
        }
    }

    return allPermissionsGranted
}

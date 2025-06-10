package com.example.android_ota_file_sync

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.ui.platform.LocalContext
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.android_ota_file_sync.data.nas.NASConnector
import com.example.android_ota_file_sync.ui.screens.main.MainScreen
import com.example.android_ota_file_sync.ui.screens.local.ImageListScreen
import com.example.android_ota_file_sync.ui.screens.nas.NASSettingsScreen
import com.example.android_ota_file_sync.ui.theme.AndroidotafilesyncTheme
import androidx.compose.runtime.*

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AndroidotafilesyncTheme {
                Surface(color = MaterialTheme.colorScheme.background) {
                    AppNavigation()
                }
            }
        }
    }
}

@Composable
fun AppNavigation() {
    val context = LocalContext.current
    val navController = rememberNavController()


    NavHost(navController, startDestination = "main") {
        composable("main") {
            var shouldNavigate by remember { mutableStateOf(false) }

            if (shouldNavigate) {
                // Kör coroutine när vi flaggar för navigation
                LaunchedEffect(Unit) {
                    val success = NASConnector.testConnection(context)
                    if (success) {
                        navController.navigate("images")
                    } else {
                        Toast.makeText(
                            context,
                            "Kunde inte ansluta till NAS",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                    shouldNavigate = false
                }
            }

            MainScreen(
                onSettingsClick = {
                    navController.navigate("settings")
                },
                onSyncClick = {
                    shouldNavigate = true
                }
            )
        }
        composable("images") {
            ImageListScreen(
                onBack = {
                    navController.popBackStack()
                }
            )
        }
        composable("settings") {
            NASSettingsScreen(
                onDone =  {
                    navController.popBackStack()
                }
            )
        }
    }
}

package com.example.android_ota_file_sync.ui.screens.local

import android.os.Environment
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.android_ota_file_sync.ui.util.rememberMediaPermissionsGranted
import java.io.File
import java.text.DecimalFormat // For formatting the MB display

data class FileStats(
    val totalCount: Int,
    val totalSize: Long,
    val byExtension: Map<String, ExtensionStat>
)


data class ExtensionStat(
    val count: Int,
    val size: Long
)

fun Long.toMegabytes(): Double {
    return this / (1024.0 * 1024.0)
}

fun calculateStats(files: List<File>): FileStats {
    val extCount = mutableMapOf<String, Int>()
    val extSize = mutableMapOf<String, Long>()
    var currentTotalSize: Long = 0

    for (file in files) {
        val ext = file.extension.lowercase()
        val fileSize = file.length()

        extCount[ext] = (extCount[ext] ?: 0) + 1
        extSize[ext] = (extSize[ext] ?: 0) + fileSize
        currentTotalSize += fileSize
    }

    val byExtensionStats = extCount.mapValues { (ext, count) ->
        ExtensionStat(count = count, size = extSize[ext] ?: 0L)
    }.toSortedMap()

    return FileStats(
        totalCount = files.size,
        totalSize = currentTotalSize,
        byExtension = byExtensionStats
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ImageListScreen(onBack: () -> Unit) {
    val context = LocalContext.current
    val permissionGranted = rememberMediaPermissionsGranted()
    val imageFiles = remember { mutableStateListOf<File>() }
    var stats by remember { mutableStateOf<FileStats?>(null) }
    val decimalFormat = remember { DecimalFormat("#,##0.0#") } // For formatting MB

    LaunchedEffect(permissionGranted) {
        if (permissionGranted) {
            val cameraDir = File(
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM),
                "Camera"
            )

            if (cameraDir.exists()) {
                val files = cameraDir.listFiles()?.filter { it.isFile } ?: emptyList()
                imageFiles.clear()
                imageFiles.addAll(files)
                stats = calculateStats(files)
            }
        }
    }
    /*
    LaunchedEffect(permissionGranted) {
        if (permissionGranted) {
            val cameraDir = Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_DCIM + "/Camera"
            )
            if (cameraDir.exists()) {
                imageFiles.clear()
                imageFiles.addAll(cameraDir.listFiles()?.filter { it.isFile } ?: emptyList())
            }
        }
    }*/


    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Lokala bilder") }, navigationIcon = {
                IconButton(onClick = onBack) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Tillbaka")
                }
            })
        }
    ) { innerPadding ->
        if (!permissionGranted) {
            Box(Modifier.padding(innerPadding).fillMaxSize(), contentAlignment = androidx.compose.ui.Alignment.Center) {
                Text("Behörighet krävs för att visa bilder.")
            }
        } else {
            if (imageFiles.isEmpty()) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                        .padding(16.dp),
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        "Inga bilder kunde hittas.",
                        style = MaterialTheme.typography.titleMedium
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        "Det kan bero på att appen bara har tillgång till enstaka bilder. " +
                                "För att tillåta full åtkomst, gå till: Inställningar > Appar > DinApp > Behörigheter > Bilder och video.",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            } else {
                LazyColumn(contentPadding = innerPadding) {
                    // Första item: Statistik
                    stats?.let { stat ->
                        item {
                            Column(modifier = Modifier.padding(16.dp)) {
                                val totalSizeMb = decimalFormat.format(stat.totalSize.toMegabytes())
                                Text(
                                    "Totalt antal filer: ${stat.totalCount} (${totalSizeMb} MB)",
                                    style = MaterialTheme.typography.titleMedium
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Text("Filändelser:", style = MaterialTheme.typography.titleSmall)
                                stat.byExtension.forEach { (ext, extStat) ->
                                    val extSizeMb = decimalFormat.format(extStat.size.toMegabytes())
                                    Text("• .$ext: ${extStat.count} st (${extSizeMb} MB)")
                                }
                                Spacer(modifier = Modifier.height(16.dp))
                            }
                        }
                    }

                    // Sen resten: Filnamn
                    items(imageFiles) { file ->
                        Text(file.name, modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp))
                    }
                }
            }
        }
    }
}

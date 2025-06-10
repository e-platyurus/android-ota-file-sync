package com.example.android_ota_file_sync.ui.screens.nas

import android.content.Context
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import com.example.android_ota_file_sync.data.nas.NASConfigStore

@Composable
fun NASSettingsScreen(
    context: Context = LocalContext.current,
    onDone: () -> Unit
) {
    var host by remember { mutableStateOf("") }
    var user by remember { mutableStateOf("") }
    var pass by remember { mutableStateOf("") }

    LaunchedEffect(Unit) {
        val (h, u, p) = NASConfigStore.load(context)
        host = h
        user = u
        pass = p
    }

    Column(modifier = Modifier.padding(16.dp)) {
        Text("NAS-inställningar", style = MaterialTheme.typography.headlineSmall)

        OutlinedTextField(
            value = host,
            onValueChange = { host = it },
            label = { Text("Host") },
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = user,
            onValueChange = { user = it },
            label = { Text("Användarnamn") },
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = pass,
            onValueChange = { pass = it },
            label = { Text("Lösenord") },
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(onClick = {
            NASConfigStore.save(context, host, user, pass)
            onDone()
        }) {
            Text("Spara")
        }
    }
}

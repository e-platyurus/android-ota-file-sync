package com.example.android_ota_file_sync.data.nas

import android.content.Context
import android.util.Log
import jcifs.CIFSContext
import jcifs.config.PropertyConfiguration
import jcifs.smb.NtlmPasswordAuthenticator
import jcifs.smb.SmbFile
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.*

object NASConnector {
    private const val TAG = "NASConnector" // For logging

    suspend fun testConnection(context: Context): Boolean {
        val (host, user, pass) = NASConfigStore.load(context)
        if (host.isEmpty()) {
            Log.w(TAG, "Connection test failed: Host is null or empty.")
            return false
        }
        if (user.isEmpty()) {
            Log.w(TAG, "Connection test failed: User is null or empty.")
            return false
        }
        if (pass.isEmpty()) {
            // Be careful logging passwords, even in a failure message.
            // Consider if this specific log is necessary or if a general "credentials incomplete" is better.
            Log.w(TAG, "Connection test failed: Password is null or empty.")
            return false
        }

        return withContext(Dispatchers.IO) {
            try {
                val props = Properties().apply {
                    setProperty("jcifs.smb.client.disableSMB1", "true")
                    setProperty("jcifs.smb.client.minVersion", "SMB202")
                    setProperty("jcifs.smb.client.maxVersion", "SMB311")
                    // Optional: Add socket timeout to prevent indefinite hangs if host is invalid but not empty
                    setProperty("jcifs.smb.client.soTimeout", "5000") // 5 seconds
                    setProperty("jcifs.smb.client.responseTimeout", "5000") // 5 seconds
                }

                val config = PropertyConfiguration(props)
                val baseContext: CIFSContext = jcifs.context.BaseContext(config)

                val auth = NtlmPasswordAuthenticator(null, user, pass)
                val authedContext = baseContext.withCredentials(auth)

                val smbUrl = "smb://$host/"
                val root = SmbFile(smbUrl, authedContext)

                root.exists()
            } catch (e: Exception) {
                e.printStackTrace()
                false
            }
        }
    }
}

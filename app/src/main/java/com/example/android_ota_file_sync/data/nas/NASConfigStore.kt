package com.example.android_ota_file_sync.data.nas

import android.content.Context
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKeys

object NASConfigStore {
    private const val PREF_NAME = "nas_config_secure"

    private const val KEY_HOST = "host"
    private const val KEY_USER = "user"
    private const val KEY_PASS = "pass"

    fun save(context: Context, host: String, user: String, pass: String) {
        val masterKey = MasterKeys.getOrCreate(MasterKeys.AES256_GCM_SPEC)
        val prefs = EncryptedSharedPreferences.create(
            PREF_NAME,
            masterKey,
            context,
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        )
        prefs.edit().apply {
            putString(KEY_HOST, host)
            putString(KEY_USER, user)
            putString(KEY_PASS, pass)
            apply()
        }
    }

    fun load(context: Context): Triple<String, String, String> {
        val masterKey = MasterKeys.getOrCreate(MasterKeys.AES256_GCM_SPEC)
        val prefs = EncryptedSharedPreferences.create(
            PREF_NAME,
            masterKey,
            context,
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        )
        val host = prefs.getString(KEY_HOST, "") ?: ""
        val user = prefs.getString(KEY_USER, "") ?: ""
        val pass = prefs.getString(KEY_PASS, "") ?: ""
        return Triple(host, user, pass)
    }
}

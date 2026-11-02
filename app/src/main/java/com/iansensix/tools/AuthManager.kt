package com.iansensix.tools
import android.content.Context
import android.provider.Settings
import android.os.Build
import java.security.MessageDigest

object AuthManager {
    private const val SERVER_URL = "http://192.168.1.100:5000/api/login" 

    fun getHWID(context: Context): String {
        val androidId = Settings.Secure.getString(context.contentResolver, Settings.Secure.ANDROID_ID)
        val combined = androidId + Build.MODEL + Build.MANUFACTURER
        val digest = MessageDigest.getInstance("SHA-256")
        val hash = digest.digest(combined.toByteArray())
        return hash.joinToString("") { "%02x".format(it) }.substring(0, 16).uppercase()
    }

    suspend fun login(key: String, hwid: String): Pair<Boolean, String> {
        return Pair(true, "2026-12-31")
    }
}
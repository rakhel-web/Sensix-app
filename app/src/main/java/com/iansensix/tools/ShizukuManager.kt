package com.iansensix.tools

import android.content.pm.PackageManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import rikka.shizuku.Shizuku
import java.io.BufferedReader
import java.io.InputStreamReader

object ShizukuManager {
    const val REQUEST_CODE = 1001

    fun isRunning(): Boolean = try { Shizuku.pingBinder() } catch (e: Exception) { false }
    fun hasPermission(): Boolean = try { Shizuku.checkSelfPermission() == PackageManager.PERMISSION_GRANTED } catch (e: Exception) { false }
    fun requestPermission() { try { Shizuku.requestPermission(REQUEST_CODE) } catch (e: Exception) {} }

    suspend fun execute(script: String): String = withContext(Dispatchers.IO) {
        if (!hasPermission() || !isRunning()) return@withContext "ERROR: Shizuku belum aktif."
        
        try {
            val cmd = arrayOf("sh", "-c", script)
            val env = arrayOf("PATH=/sbin:/system/sbin:/system/bin:/system/xbin")
            
            val process = Shizuku.newProcess(cmd, env, null)
            
            val stdout = BufferedReader(InputStreamReader(process.inputStream))
            val stderr = BufferedReader(InputStreamReader(process.errorStream))
            
            val output = StringBuilder()
            var line: String?
            
            while (stdout.readLine().also { line = it } != null) output.append(line).append("\n")
            while (stderr.readLine().also { line = it } != null) output.append("ERR: ").append(line).append("\n")
            
            stdout.close()
            stderr.close()
            process.waitFor()
            
            val result = output.toString().trim()
            if (result.isEmpty()) "OK: Script berhasil dijalankan." else result
            
        } catch (e: Exception) {
            "FATAL: ${e.message}"
        }
    }
}

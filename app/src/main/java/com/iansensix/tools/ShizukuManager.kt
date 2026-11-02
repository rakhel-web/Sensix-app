package com.iansensix.tools

import android.content.pm.PackageManager
import android.os.ParcelFileDescriptor
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
            // Pake arrayOf biar tipe datanya jelas
            val cmd = arrayOf("sh", "-c", script)
            val env = arrayOf("PATH=/sbin:/system/sbin:/system/bin:/system/xbin")
            
            // Panggil Shizuku.newProcess
            val pfd = Shizuku.newProcess(cmd, env, null)
            
            // Baca output stdout dan stderr
            val stdout = BufferedReader(InputStreamReader(ParcelFileDescriptor.AutoCloseInputStream(pfd[0])))
            val stderr = BufferedReader(InputStreamReader(ParcelFileDescriptor.AutoCloseInputStream(pfd[2])))
            
            val output = StringBuilder()
            var line: String?
            
            // Pake .also biar aman dari warning compiler Kotlin baru
            while (stdout.readLine().also { line = it } != null) {
                output.append(line).append("\n")
            }
            while (stderr.readLine().also { line = it } != null) {
                output.append("ERR: ").append(line).append("\n")
            }
            
            stdout.close()
            stderr.close()
            
            output.toString().ifEmpty { "OK: Script berhasil." }
        } catch (e: Exception) { 
            "FATAL: ${e.message}" 
        }
    }
}
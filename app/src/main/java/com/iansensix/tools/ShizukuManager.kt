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

    fun isRunning(): Boolean = try { 
        Shizuku.pingBinder() 
    } catch (e: Exception) { 
        false 
    }

    fun hasPermission(): Boolean = try { 
        Shizuku.checkSelfPermission() == PackageManager.PERMISSION_GRANTED 
    } catch (e: Exception) { 
        false 
    }

    fun requestPermission() { 
        try { 
            Shizuku.requestPermission(REQUEST_CODE) 
        } catch (e: Exception) {
            e.printStackTrace()
        } 
    }

    suspend fun execute(script: String): String = withContext(Dispatchers.IO) {
        if (!hasPermission() || !isRunning()) {
            return@withContext "ERROR: Shizuku belum aktif."
        }
        
        try {
            val pfd = Shizuku.newProcess(
                arrayOf("sh", "-c", script),
                arrayOf("PATH=/sbin:/system/sbin:/system/bin:/system/xbin"),
                null
            )
            
            val stdout = BufferedReader(InputStreamReader(ParcelFileDescriptor.AutoCloseInputStream(pfd[0])))
            val stderr = BufferedReader(InputStreamReader(ParcelFileDescriptor.AutoCloseInputStream(pfd[2])))
            
            val output = StringBuilder()
            
            // Pake list buat nampung baris biar aman dari error compiler
            val outLines = stdout.readLines()
            for (line in outLines) {
                output.append(line).append("\n")
            }

            val errLines = stderr.readLines()
            for (line in errLines) {
                output.append("ERR: ").append(line).append("\n")
            }
            
            stdout.close()
            stderr.close()
            
            val finalStr = output.toString()
            if (finalStr.isEmpty()) "OK: Script berhasil." else finalStr

        } catch (e: Exception) {
            "FATAL: ${e.message}"
        }
    }
}
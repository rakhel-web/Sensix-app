package com.iansensix.tools

import java.io.RandomAccessFile
import java.net.InetAddress

object MonitorEngine {
    fun getCpuLoad(): String {
        try {
            val reader = RandomAccessFile("/proc/stat", "r")
            val line = reader.readLine(); reader.close()
            val parts = line.split("\\s+".toRegex())
            val idle = parts[4].toLong()
            val total = parts.drop(1).take(7).sumOf { it.toLong() }
            return String.format("%.1f%%", (1.0 - idle.toDouble() / total) * 100)
        } catch (e: Exception) { return "N/A" }
    }
    
    fun getRamUsage(context: android.content.Context): String {
        val am = context.getSystemService(android.content.Context.ACTIVITY_SERVICE) as android.app.ActivityManager
        val mi = android.app.ActivityManager.MemoryInfo(); am.getMemoryInfo(mi)
        return String.format("%d MB", (mi.totalMem - mi.availMem) / (1024 * 1024))
    }
    
    fun getPing(): String {
        try {
            val start = System.currentTimeMillis()
            if (InetAddress.getByName("8.8.8.8").isReachable(1000)) return "${System.currentTimeMillis() - start} ms"
        } catch (e: Exception) {}
        return "Timeout"
    }
}

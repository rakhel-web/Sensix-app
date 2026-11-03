package com.iansensix.tools

import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch
import rikka.shizuku.Shizuku

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        try {
            setContentView(R.layout.activity_main)
            
            val tvStatus = findViewById<TextView>(R.id.tvStatus)
            val tvLog = findViewById<TextView>(R.id.tvLog)

            Shizuku.addRequestPermissionResultListener { requestCode, grantResult ->
                if (requestCode == ShizukuManager.REQUEST_CODE) {
                    tvStatus.text = "Status: Connected (UID 2000)"
                    tvStatus.setTextColor(android.graphics.Color.parseColor("#00E676"))
                }
            }

            if (ShizukuManager.hasPermission()) {
                tvStatus.text = "Status: Connected (UID 2000)"
                tvStatus.setTextColor(android.graphics.Color.parseColor("#00E676"))
            } else {
                ShizukuManager.requestPermission()
            }

            fun execute(script: String) {
                if (!ShizukuManager.hasPermission()) {
                    Toast.makeText(this, "Shizuku belum aktif!", Toast.LENGTH_SHORT).show()
                    return
                }
                lifecycleScope.launch {
                    tvLog.text = ShizukuManager.execute(script)
                }
            }

            findViewById<Button>(R.id.btnSmoothInject).setOnClickListener { execute(TweakScripts.getApplyScript()) }
            findViewById<Button>(R.id.btnSmoothDrag).setOnClickListener { execute(TweakScripts.getDragScript()) }
            findViewById<Button>(R.id.btnRestore).setOnClickListener { execute(TweakScripts.getRestoreScript()) }

        } catch (e: Exception) {
            Toast.makeText(this, "Error: ${e.message}", Toast.LENGTH_LONG).show()
        }
    }
}

package com.iansensix.tools

import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import rikka.shizuku.Shizuku

class MainActivity : AppCompatActivity() {
    private lateinit var tabHome: ScrollView
    private lateinit var tabProfile: ScrollView
    private lateinit var tabSettings: ScrollView
    private lateinit var navHome: LinearLayout
    private lateinit var navProfile: LinearLayout
    private lateinit var navSettings: LinearLayout
    private lateinit var tvStatus: TextView
    private lateinit var tvLog: TextView
    private lateinit var seekSensiX: SeekBar
    private lateinit var seekSensiY: SeekBar
    private lateinit var tvSensiX: TextView
    private lateinit var tvSensiY: TextView
    private lateinit var tvCpu: TextView
    private lateinit var tvGpu: TextView
    private lateinit var tvRam: TextView
    private lateinit var tvPing: TextView
    private var isMonitoring = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        try {
            setContentView(R.layout.activity_main)
            initViews()
            setupNavigation()
            setupSensiSliders()
            setupSocialButtons()
            setupFreeFireLauncher()
            setupShizukuListener()
            
            findViewById<TextView>(R.id.tvKeyDisplay).text = "ianSENSIX-PRO"
            findViewById<TextView>(R.id.tvExpired).text = "Berlaku Sampai: 2026-12-31"
        } catch (e: Exception) {
            Toast.makeText(this, "Startup Error: ${e.message}", Toast.LENGTH_LONG).show()
        }
    }

    private fun initViews() {
        tabHome = findViewById(R.id.tabHome); tabProfile = findViewById(R.id.tabProfile); tabSettings = findViewById(R.id.tabSettings)
        navHome = findViewById(R.id.navHome); navProfile = findViewById(R.id.navProfile); navSettings = findViewById(R.id.navSettings)
        tvStatus = findViewById(R.id.tvStatus); tvLog = findViewById(R.id.tvLog)
        seekSensiX = findViewById(R.id.seekSensiX); seekSensiY = findViewById(R.id.seekSensiY)
        tvSensiX = findViewById(R.id.tvSensiX); tvSensiY = findViewById(R.id.tvSensiY)
        tvCpu = findViewById(R.id.tvCpu); tvGpu = findViewById(R.id.tvGpu); tvRam = findViewById(R.id.tvRam); tvPing = findViewById(R.id.tvPing)

        findViewById<Button>(R.id.btnSmoothInject).setOnClickListener { runScript(TweakScripts.getApplyScript()) }
        findViewById<Button>(R.id.btnSmoothDrag).setOnClickListener { runScript(TweakScripts.getDragScript()) }
        findViewById<Button>(R.id.btnRestore).setOnClickListener { runScript(TweakScripts.getRestoreScript()) }
    }

    private fun setupNavigation() {
        navHome.setOnClickListener { switchTab(0) }
        navProfile.setOnClickListener { switchTab(1) }
        navSettings.setOnClickListener { switchTab(2) }
        findViewById<Button>(R.id.btnGoToInject).setOnClickListener { switchTab(2) }
    }

    private fun switchTab(index: Int) {
        tabHome.visibility = View.GONE; tabProfile.visibility = View.GONE; tabSettings.visibility = View.GONE
        val inactive = Color.parseColor("#8B949E"); val active = Color.parseColor("#00E676")
        val activeBg = resources.getDrawable(R.drawable.bg_nav_indicator, theme)

        listOf(navHome, navProfile, navSettings).forEach { nav ->
            nav.background = null
            (nav.getChildAt(0) as TextView).setTextColor(inactive)
            (nav.getChildAt(1) as TextView).setTextColor(inactive)
        }

        when (index) {
            0 -> {
                tabHome.visibility = View.VISIBLE; navHome.background = activeBg
                (navHome.getChildAt(0) as TextView).setTextColor(active); (navHome.getChildAt(1) as TextView).setTextColor(active)
            }
            1 -> {
                tabProfile.visibility = View.VISIBLE; navProfile.background = activeBg
                (navProfile.getChildAt(0) as TextView).setTextColor(active); (navProfile.getChildAt(1) as TextView).setTextColor(active)
                startMonitoring()
            }
            2 -> {
                tabSettings.visibility = View.VISIBLE; navSettings.background = activeBg
                (navSettings.getChildAt(0) as TextView).setTextColor(active); (navSettings.getChildAt(1) as TextView).setTextColor(active)
            }
        }
    }

    private fun setupSensiSliders() {
        seekSensiX.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(s: SeekBar?, p: Int, f: Boolean) { tvSensiX.text = "Sensi X : $p" }
            override fun onStartTrackingTouch(s: SeekBar?) {}
            override fun onStopTrackingTouch(s: SeekBar?) {}
        })
        seekSensiY.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(s: SeekBar?, p: Int, f: Boolean) { tvSensiY.text = "Sensi Y : $p" }
            override fun onStartTrackingTouch(s: SeekBar?) {}
            override fun onStopTrackingTouch(s: SeekBar?) {}
        })
        findViewById<Button>(R.id.btnApplySensi).setOnClickListener {
            val x = seekSensiX.progress; val y = seekSensiY.progress
            runScript(TweakScripts.getSensiScript(x, y))
            Toast.makeText(this, "Sensitivitas X:$x Y:$y Diterapkan!", Toast.LENGTH_SHORT).show()
        }
    }

    private fun setupSocialButtons() {
        val waNumber = "6281234567890"
        val tiktokUser = "ianSENSIX"
        findViewById<TextView>(R.id.btnWa).setOnClickListener {
            try { startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("https://wa.me/$waNumber"))) } 
            catch (e: Exception) { Toast.makeText(this, "WhatsApp tidak ditemukan", Toast.LENGTH_SHORT).show() }
        }
        findViewById<TextView>(R.id.btnTiktok).setOnClickListener {
            try { startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("https://www.tiktok.com/@$tiktokUser"))) } 
            catch (e: Exception) { Toast.makeText(this, "Gagal membuka TikTok", Toast.LENGTH_SHORT).show() }
        }
    }

    private fun setupFreeFireLauncher() {
        findViewById<LinearLayout>(R.id.cardFreeFire).setOnClickListener {
            try {
                val intent = packageManager.getLaunchIntentForPackage("com.dts.freefireth")
                if (intent != null) startActivity(intent) else Toast.makeText(this, "FF tidak terinstall", Toast.LENGTH_SHORT).show()
            } catch (e: Exception) {
                Toast.makeText(this, "Gagal buka game", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun startMonitoring() {
        if (isMonitoring) return
        isMonitoring = true
        lifecycleScope.launch {
            while (isMonitoring && tabProfile.visibility == View.VISIBLE) {
                try {
                    tvCpu.text = MonitorEngine.getCpuLoad()
                    tvRam.text = MonitorEngine.getRamUsage(this@MainActivity)
                    tvGpu.text = MonitorEngine.getGpuLoad()
                    tvPing.text = MonitorEngine.getPing()
                } catch (e: Exception) {
                    // Abaikan error monitoring biar app gak crash
                }
                delay(1500L)
            }
            isMonitoring = false
        }
    }

    private fun setupShizukuListener() {
        try {
            Shizuku.addRequestPermissionResultListener { requestCode, grantResult ->
                if (requestCode == ShizukuManager.REQUEST_CODE) updateShizukuStatus()
            }
            updateShizukuStatus()
        } catch (e: Exception) {
            tvStatus.text = "Status: Gagal inisialisasi Shizuku"
        }
    }

    private fun updateShizukuStatus() {
        try {
            if (!ShizukuManager.isRunning()) {
                tvStatus.text = "Status: Shizuku OFF"
                tvStatus.setTextColor(Color.RED)
            } else if (!ShizukuManager.hasPermission()) {
                tvStatus.text = "Status: Meminta izin Shizuku..."
                tvStatus.setTextColor(Color.YELLOW)
                ShizukuManager.requestPermission()
            } else {
                tvStatus.text = "Status: Connected (UID 2000)"
                tvStatus.setTextColor(Color.parseColor("#00E676"))
            }
        } catch (e: Exception) {}
    }

    private fun runScript(script: String) {
        if (!ShizukuManager.hasPermission()) {
            Toast.makeText(this, "Shizuku belum aktif!", Toast.LENGTH_SHORT).show()
            return
        }
        tvLog.text = "Mengeksekusi..."
        lifecycleScope.launch {
            try {
                tvLog.text = ShizukuManager.execute(script)
            } catch (e: Exception) {
                tvLog.text = "Gagal eksekusi: ${e.message}"
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        isMonitoring = false
    }
}
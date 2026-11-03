package com.iansensix.tools

object TweakScripts {
    
    // 1. Tweak buat Optimasi Layar (Inject Smooth)
    fun getApplyScript() = """
        settings put system peak_refresh_rate 120
        settings put system min_refresh_rate 90
        settings put global window_animation_scale 0.0
        settings put global transition_animation_scale 0.0
        settings put global animator_duration_scale 0.0
        settings put global game_optimization_switch 1
        cmd game mode performance com.dts.freefireth
        setprop debug.sf.enable_gl_backpressure 0
        echo "Inject Smooth Applied!"
    """.trimIndent()

    // 2. Tweak buat Aim/Drag (Smooth Drag Extreme - Slop 1 + Fix Limit)
    fun getDragScript() = """
        # 1. Jeda sentuh singkat
        settings put secure long_press_timeout 150
        settings put secure multi_press_timeout 100
        
        # 2. Touch Slop 1
        settings put secure touch_slop 1
        
        # 3. Bebasin filter sentuhan
        settings put secure touch_blocking_period 0
        
        # 4. Pointer speed maksimal (angka 7 biar gak error)
        settings put system pointer_speed 7
        
        # 5. Prioritas game
        cmd game mode performance com.dts.freefireth
        cmd game set --mode performance --downscale 1.0 com.dts.freefireth
        
        # 6. Velocity fling (Bikin Drag makin jauh & cepet)
        setprop debug.input.flinger_velocity_scale_x 2.0
        setprop debug.input.flinger_velocity_scale_y 2.0
        
        # 7. Bypass touch latency render (buat aim nempel)
        setprop debug.hwui.disable_vsync true
        setprop debug.choreographer.skipwarning 1
        
        echo "Smooth Drag Extreme Applied!"
    """.trimIndent()

    // 3. Tweak buat balikin ke setelan awal (Restore)
    fun getRestoreScript() = """
        settings delete system peak_refresh_rate
        settings delete system min_refresh_rate
        settings put global window_animation_scale 1.0
        settings put global transition_animation_scale 1.0
        settings put global animator_duration_scale 1.0
        settings put secure long_press_timeout 400
        settings put secure multi_press_timeout 300
        settings put secure touch_slop 8
        settings put secure touch_blocking_period 400
        settings put system pointer_speed 0
        echo "Restore Default Success!"
    """.trimIndent()
}

package com.iansensix.tools
object TweakScripts {
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

    fun getDragScript() = """
        settings put secure long_press_timeout 200
        settings put secure multi_press_timeout 150
        settings put secure touch_blocking_period 0
        settings put system pointer_speed 7
        echo "Smooth Drag Applied!"
    """.trimIndent()

    fun getRestoreScript() = """
        settings delete system peak_refresh_rate
        settings delete system min_refresh_rate
        settings put global window_animation_scale 1.0
        settings put global transition_animation_scale 1.0
        settings put global animator_duration_scale 1.0
        settings put secure long_press_timeout 400
        settings put secure multi_press_timeout 300
        settings put secure touch_blocking_period 400
        settings put system pointer_speed 0
        echo "Restore Default Success!"
    """.trimIndent()

    fun getSensiScript(sensiX: Int, sensiY: Int): String {
        val xValue = (sensiX - 100) / 15
        val slopValue = (16 - (sensiY / 15)).coerceAtLeast(1)
        return """
            settings put system pointer_speed $xValue
            settings put secure touch_slop $slopValue
            settings put secure touch_blocking_period 0
            echo "Sensitivitas X=$sensiX Y=$sensiY diterapkan."
        """.trimIndent()
    }
}
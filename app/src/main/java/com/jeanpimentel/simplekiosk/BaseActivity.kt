package com.jeanpimentel.simplekiosk

import android.content.Context
import android.content.Intent
import android.graphics.PixelFormat
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.Gravity
import android.view.KeyEvent
import android.view.View
import android.view.WindowManager

abstract class BaseActivity : AppCompatActivity() {

    private val blockedKeys = listOf(KeyEvent.KEYCODE_VOLUME_DOWN, KeyEvent.KEYCODE_VOLUME_UP)
    private val keySequences = mutableListOf<Int>()
    private val keySequencesExpected = listOf(KeyEvent.KEYCODE_VOLUME_UP, KeyEvent.KEYCODE_VOLUME_UP, KeyEvent.KEYCODE_VOLUME_UP)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        window.addFlags(WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD)

        preventStatusBarExpansion()
    }

    override fun onResume() {
        super.onResume()
        setWindowFlags()
    }

    override fun onBackPressed() {}

    override fun dispatchKeyEvent(event: KeyEvent?): Boolean {
        if (event == null) {
            return super.dispatchKeyEvent(event)
        }

        if (blockedKeys.contains(event.keyCode)) {

            if (event.action == KeyEvent.ACTION_UP) {

                if (event.keyCode == KeyEvent.KEYCODE_VOLUME_DOWN) {
                    keySequences.clear()
                }

                if (event.keyCode == KeyEvent.KEYCODE_VOLUME_UP) {
                    keySequences.add(KeyEvent.KEYCODE_VOLUME_UP)
                }

                if (keySequences == keySequencesExpected) {
                    (applicationContext as? KioskApplication)?.deactiveKioskMode()
                    finishAffinity()
                }
            }

            return true
        }

        return super.dispatchKeyEvent(event)
    }

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        if (!hasFocus) {
            sendBroadcast(Intent(Intent.ACTION_CLOSE_SYSTEM_DIALOGS))
        }

        setWindowFlags()
    }


    private fun setWindowFlags() {
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_STABLE or
                View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION or
                View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or
                View.SYSTEM_UI_FLAG_HIDE_NAVIGATION or
                View.SYSTEM_UI_FLAG_FULLSCREEN or
                View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
    }

    private fun preventStatusBarExpansion() {
        val manager = applicationContext.getSystemService(Context.WINDOW_SERVICE) as WindowManager

        val localLayoutParams = WindowManager.LayoutParams()
        localLayoutParams.type = WindowManager.LayoutParams.TYPE_SYSTEM_ERROR
        localLayoutParams.gravity = Gravity.TOP
        localLayoutParams.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL or WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN

        localLayoutParams.width = WindowManager.LayoutParams.MATCH_PARENT

        val resId = resources.getIdentifier("status_bar_height", "dimen", "android")
        val result = if (resId > 0) {
            resources.getDimensionPixelSize(resId)
        } else {
            60  // Use Fallback size:
        }

        localLayoutParams.height = result
        localLayoutParams.format = PixelFormat.TRANSPARENT

        val view = InterceptViewGroup(this)
        manager.addView(view, localLayoutParams)
    }
}

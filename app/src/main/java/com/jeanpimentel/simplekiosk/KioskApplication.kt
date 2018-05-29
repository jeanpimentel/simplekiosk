package com.jeanpimentel.simplekiosk

import android.annotation.SuppressLint
import android.app.Activity
import android.app.Application
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.os.PowerManager
import android.preference.PreferenceManager


class KioskApplication : Application() {

    private val KIOSK_MODE = "kiosk_mode"

    private lateinit var instance: KioskApplication
    private var wakeLock: PowerManager.WakeLock? = null
    private var onScreenOffReceiver: OnScreenOffReceiver? = null
    private var isActivityPaused = false

    override fun onCreate() {
        super.onCreate()
        instance = this
        activeKioskMode()
        registerKioskModeScreenOffReceiver()
        startKioskService()
        registerLifecycle()
    }

    private fun registerLifecycle() {
        registerActivityLifecycleCallbacks(object : Application.ActivityLifecycleCallbacks {
            override fun onActivityResumed(p0: Activity?) {
                isActivityPaused = false
            }

            override fun onActivityPaused(p0: Activity?) {
                isActivityPaused = true
            }

            override fun onActivityStarted(p0: Activity?) {}
            override fun onActivityDestroyed(p0: Activity?) {}
            override fun onActivitySaveInstanceState(p0: Activity?, p1: Bundle?) {}
            override fun onActivityStopped(p0: Activity?) {}
            override fun onActivityCreated(p0: Activity?, p1: Bundle?) {}
        })
    }

    fun getWakeLock(): PowerManager.WakeLock? {
        if (wakeLock == null) {
            // lazy loading: first call, create wakeLock via PowerManager.
            val pm = getSystemService(Context.POWER_SERVICE) as PowerManager
            wakeLock = pm.newWakeLock(PowerManager.FULL_WAKE_LOCK or PowerManager.ACQUIRE_CAUSES_WAKEUP, "wakeup")
        }
        return wakeLock
    }

    fun isKioskModeActive(): Boolean {
        val sp = PreferenceManager.getDefaultSharedPreferences(applicationContext)
        return sp.getBoolean(KIOSK_MODE, false)
    }

    @SuppressLint("ApplySharedPref")
    fun activeKioskMode() {
        val sp = PreferenceManager.getDefaultSharedPreferences(applicationContext)
        sp.edit().putBoolean(KIOSK_MODE, true).commit()
    }

    @SuppressLint("ApplySharedPref")
    fun deactiveKioskMode() {
        val sp = PreferenceManager.getDefaultSharedPreferences(applicationContext)
        sp.edit().putBoolean(KIOSK_MODE, false).commit()
    }

    fun isActivityPaused(): Boolean {
        return isActivityPaused
    }

    private fun registerKioskModeScreenOffReceiver() {
        // register screen off receiver
        val filter = IntentFilter(Intent.ACTION_SCREEN_OFF)
        onScreenOffReceiver = OnScreenOffReceiver()
        registerReceiver(onScreenOffReceiver, filter)
    }

    private fun startKioskService() { // ... and this method
        startService(Intent(this, KioskService::class.java))
    }
}
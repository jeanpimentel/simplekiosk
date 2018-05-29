package com.jeanpimentel.simplekiosk

import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.IBinder


class KioskService : Service() {

    private val INTERVAL = 2000L // periodic interval to check in seconds -> 2 seconds

    private var thread: Thread? = null
    private var context: Context? = null
    private var running = false

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        running = true
        context = this

        thread = Thread(Runnable {
            do {
                checkKioskMode()
                try {
                    Thread.sleep(INTERVAL)
                } catch (e: InterruptedException) {
                }

            } while (running)
            stopSelf()
        })

        thread?.start()
        return Service.START_NOT_STICKY
    }

    override fun onDestroy() {
        running = false
        super.onDestroy()
    }

    override fun onBind(intent: Intent): IBinder? {
        return null
    }

    private fun checkKioskMode() {
        val application: KioskApplication? = context?.applicationContext as? KioskApplication

        if (application?.isKioskModeActive() == false) {
            running = false
        }

        if (application?.isActivityPaused() == true && running) {
            restoreApp()
        }
    }

    private fun restoreApp() {
        context?.startActivity(Intent(context, MainActivity::class.java).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        })
    }
}

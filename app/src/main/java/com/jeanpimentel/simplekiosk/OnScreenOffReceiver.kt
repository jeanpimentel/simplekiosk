package com.jeanpimentel.simplekiosk

import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent


class OnScreenOffReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        if (Intent.ACTION_SCREEN_OFF == intent.action) {
            wakeUpDevice(context.applicationContext as? KioskApplication)
        }
    }

    @SuppressLint("WakelockTimeout")
    private fun wakeUpDevice(context: KioskApplication?) {
        val wakeLock = context?.getWakeLock() ?: return

        if (wakeLock.isHeld) {
            // release old wake lock
            wakeLock.release()
        }

        // create a new wake lock...
        wakeLock.acquire()

        // ... and release again
        wakeLock.release()
    }

}

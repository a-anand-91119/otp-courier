package dev.notyouraverage.otpcourier.applications

import android.app.Application
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.util.Log
import dev.notyouraverage.otpcourier.constants.Constants.NOTIFICATION_CHANNEL_GENERAL

class MainApplication : Application() {
    companion object {
        private val TAG by lazy { MainApplication::class.java.simpleName }
    }

    override fun onCreate() {
        super.onCreate()
        Log.i(TAG, "MainApplication::onCreate")
        val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        with(
            NotificationChannel(
                NOTIFICATION_CHANNEL_GENERAL,
                "OTP Courier",
                NotificationManager.IMPORTANCE_HIGH,
            ),
        ) {
            enableLights(false)
            setShowBadge(false)
            enableVibration(false)
            setSound(null, null)
            lockscreenVisibility = Notification.VISIBILITY_PUBLIC
            manager.createNotificationChannel(this)
        }
    }

    override fun onTerminate() {
        Log.i(TAG, "MainApplication::onTerminate")
        super.onTerminate()
    }
}

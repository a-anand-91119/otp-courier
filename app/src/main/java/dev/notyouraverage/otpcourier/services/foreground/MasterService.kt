package dev.notyouraverage.otpcourier.services.foreground

import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.provider.Telephony
import android.util.Log
import android.widget.Toast
import androidx.core.app.NotificationCompat
import dev.notyouraverage.otpcourier.R
import dev.notyouraverage.otpcourier.constants.Constants
import dev.notyouraverage.otpcourier.constants.Constants.CODE_FOREGROUND_SERVICE
import dev.notyouraverage.otpcourier.constants.Constants.NOTIFICATION_CHANNEL_GENERAL
import dev.notyouraverage.otpcourier.models.SmsMessageData
import dev.notyouraverage.otpcourier.receivers.SmsReceiver
import dev.notyouraverage.otpcourier.services.background.SmsService


class MasterService : Service() {

    private lateinit var smsReceiver: SmsReceiver
    private var backgroundServiceRunning = false

    private val stopDelay: Long = 5 * 60 * 1000
    private val handler: Handler = Handler(Looper.getMainLooper())

    companion object {
        private val TAG by lazy { MasterService::class.java.simpleName }
        const val SMS_DATA = "SMS_DATA"
        const val START_SELF = "START_SELF"
        const val STOP_SELF = "STOP_SELF"
        const val START_BACKGROUND = "START_BACKGROUND"
        const val STOP_BACKGROUND = "STOP_BACKGROUND"
        const val SEND_DATA = "SEND_DATA"
    }

    override fun onBind(p0: Intent?): IBinder? = null

    override fun onCreate() {
        super.onCreate()
        Log.i(TAG, "MasterService::onCreate")
        if (this::smsReceiver.isInitialized) return
    }

    override fun onDestroy() {
        Toast.makeText(this, "Killing Foreground Service", Toast.LENGTH_SHORT).show()
        Log.i(TAG, "MasterService::onDestroy")
        unregisterReceiver(smsReceiver)
        stopBackgroundService()
        super.onDestroy()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.i(TAG, "MasterService::onStartCommand")
        when (intent?.action) {
            START_SELF -> startSelf(intent.extras)
            STOP_SELF -> stopSelf()
            START_BACKGROUND -> startBackgroundService()
            STOP_BACKGROUND -> stopBackgroundService()
            SEND_DATA -> sendToBackgroundService(
                intent.getParcelableExtra(
                    SMS_DATA,
                    SmsMessageData::class.java
                )
            )
        }
        return super.onStartCommand(intent, flags, startId)
    }

    private fun sendToBackgroundService(smsMessageData: SmsMessageData?) {
        if (!backgroundServiceRunning || smsMessageData == null) return
        Intent(this, SmsService::class.java).also {
            it.action = SmsService.SEND_SMS
            it.putExtra(SmsService.SMS_DATA, smsMessageData)
            startService(it)
        }
    }

    private fun stopBackgroundService() {
        if (!backgroundServiceRunning) return
        Intent(this, SmsService::class.java).also {
            baseContext.stopService(it)
            backgroundServiceRunning = false

            handler.removeCallbacksAndMessages(null)
        }
    }

    private fun startBackgroundService() {
        if (backgroundServiceRunning) return
        Intent(this, SmsService::class.java).also {
            baseContext.startService(it)
            backgroundServiceRunning = true

            handler.postDelayed({
                stopBackgroundService()
            }, stopDelay)
        }
    }

    private fun startSelf(extras: Bundle?) {
        smsReceiver = SmsReceiver(
            extras?.getString(Constants.SECRET_PASSWORD) ?: "",
            extras?.getString(Constants.WHITE_LISTED_CONTACT_NUMBER) ?: ""
        )
        baseContext.registerReceiver(
            smsReceiver,
            IntentFilter(Telephony.Sms.Intents.SMS_RECEIVED_ACTION)
        )

        Log.i(TAG, "MasterService::startingForegroundService")
        Toast.makeText(this, "Starting Foreground Service", Toast.LENGTH_SHORT).show()
        with(NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_GENERAL)) {
            setTicker(null)
            setContentTitle("OTP Courier")
            setContentText("OTP Courier is running")
            setAutoCancel(false)
            setOngoing(true)
            setWhen(System.currentTimeMillis())
            setSmallIcon(R.drawable.ic_launcher_foreground)
            priority = NotificationManager.IMPORTANCE_HIGH
            startForeground(CODE_FOREGROUND_SERVICE, build())
        }
    }

}
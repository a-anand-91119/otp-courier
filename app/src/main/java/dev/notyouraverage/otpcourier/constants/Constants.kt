package dev.notyouraverage.otpcourier.constants

import android.Manifest

object Constants {
    val SMS_PERMISSIONS = listOf(
        Manifest.permission.READ_SMS,
        Manifest.permission.SEND_SMS,
        Manifest.permission.RECEIVE_SMS
    )
    val PHONE_PERMISSIONS =
        listOf(Manifest.permission.READ_PHONE_STATE, Manifest.permission.CALL_PHONE)
    val SERVICE_STATE_PERMISSIONS =
        listOf(Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.READ_PHONE_STATE)

    const val NOTIFICATION_CHANNEL_GENERAL = "ListeningNotificationChannel"
    const val CODE_FOREGROUND_SERVICE = 1
    const val CODE_REPLY_INTENT = 2
    const val CODE_ACHIEVE_INTENT = 3

    const val INTENT_COMMAND = "Command"
    const val INTENT_COMMAND_EXIT = "Exit"
    const val INTENT_COMMAND_REPLY = "Reply"
    const val INTENT_COMMAND_ACHIEVE = "Achieve"
}
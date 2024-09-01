package dev.notyouraverage.otpcourier.receivers

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.provider.Telephony
import android.telephony.SmsMessage
import android.util.Log
import dev.notyouraverage.otpcourier.enums.SmsCommand
import dev.notyouraverage.otpcourier.models.SmsMessageData
import dev.notyouraverage.otpcourier.services.foreground.MasterService

class SmsReceiver : BroadcastReceiver() {

    companion object {
        private val TAG by lazy { SmsReceiver::class.java.simpleName }
    }

    private val targetContacts = listOf("123", "123123")
    private val commandPattern = Regex("OTPC\\s+(start|stop)\\s+(\\S+)")
    private val secretPassword = "123123"

    override fun onReceive(context: Context?, intent: Intent?) {
        if (intent?.action != Telephony.Sms.Intents.SMS_RECEIVED_ACTION) return

        val extractMessages = Telephony.Sms.Intents.getMessagesFromIntent(intent)
        val commandMessage =
            extractMessages.filter { message -> targetContacts.contains(message.originatingAddress) }
                .firstOrNull { message -> matchesCommandPattern(message.messageBody.trim()) }

        if (commandMessage != null) {
            val smsCommand =
                extractCommandAndPassword(
                    commandMessage.originatingAddress,
                    commandMessage.messageBody
                )
            if (smsCommand?.secretPassword != secretPassword) return

            when (smsCommand.command?.uppercase()) {
                SmsCommand.START.toString() -> {
                    sendToMasterService(
                        context,
                        MasterService.START_BACKGROUND,
                        null
                    )
                }

                SmsCommand.STOP.toString() -> {
                    sendToMasterService(
                        context,
                        MasterService.STOP_BACKGROUND,
                        null
                    )
                }
            }
            Log.v(TAG, smsCommand.toString())
        } else {
            extractMessages.map(this::getSmsMessageData)
                .forEach { smsMessageData ->
                    sendToMasterService(
                        context,
                        MasterService.SEND_DATA,
                        smsMessageData
                    )
                }
        }
    }

    private fun getSmsMessageData(smsMessage: SmsMessage?): SmsMessageData {
        return SmsMessageData(
            sender = smsMessage?.originatingAddress ?: "Unknown",
            command = SmsCommand.SEND_TO_WORKER.toString(),
            rawMessage = smsMessage?.messageBody ?: "",
            secretPassword = "null"
        )
    }

    private fun sendToMasterService(
        context: Context?,
        action: String,
        smsMessageData: SmsMessageData?
    ) {
        Log.i(TAG, smsMessageData.toString())
        Intent(context, MasterService::class.java).also {
            it.setAction(action)
            it.putExtra(MasterService.SMS_DATA, smsMessageData)
            context?.startService(it)
        }
    }

    private fun extractCommandAndPassword(
        originatingAddress: String?,
        messageBody: String?
    ): SmsMessageData? {
        val matchResult = messageBody?.trim()?.let { commandPattern.find(it) }

        return if (matchResult != null && matchResult.groupValues.size == 3) {
            SmsMessageData(
                sender = originatingAddress ?: "Unknown",
                command = matchResult.groupValues[1],
                rawMessage = messageBody,
                secretPassword = matchResult.groupValues[2]
            )
        } else null

    }

    private fun matchesCommandPattern(message: String): Boolean {
        return message.matches(commandPattern)
    }
}

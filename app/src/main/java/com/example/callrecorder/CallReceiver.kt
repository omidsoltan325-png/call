package com.example.callrecorder

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.telephony.TelephonyManager

class CallReceiver : BroadcastReceiver() {

    companion object {
        private var wasRinging = false
    }

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action != "android.intent.action.PHONE_STATE") return

        val state = intent.getStringExtra(TelephonyManager.EXTRA_STATE)

        when (state) {
            TelephonyManager.EXTRA_STATE_RINGING -> {
                wasRinging = true
            }
            TelephonyManager.EXTRA_STATE_OFFHOOK -> {
                val serviceIntent = Intent(context, CallRecordingService::class.java)
                serviceIntent.action = CallRecordingService.ACTION_START
                context.startForegroundService(serviceIntent)
                wasRinging = false
            }
            TelephonyManager.EXTRA_STATE_IDLE -> {
                val serviceIntent = Intent(context, CallRecordingService::class.java)
                serviceIntent.action = CallRecordingService.ACTION_STOP
                context.startService(serviceIntent)
                wasRinging = false
            }
        }
    }
}

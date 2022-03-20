package com.simple.alarm

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import java.util.logging.Logger

class AlarmReceiver : BroadcastReceiver() {

    private val logger = Logger.getLogger("adadad")

    override fun onReceive(context: Context, intent: Intent) {
        logger.info("AlarmReceiver onReceive")
        when (intent.action) {
            MainActivity.ALARM_FIRE, MainActivity.ALARM_DISMISS -> {
                context.startForegroundService(Intent(context, AlarmService::class.java).apply { action = intent.action })
            }
        }
    }
}

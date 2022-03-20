package com.simple.alarm

import android.app.*
import android.content.Context
import android.content.Intent
import android.media.Ringtone
import android.media.RingtoneManager
import android.os.IBinder
import android.os.VibrationEffect
import android.os.Vibrator
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.simple.alarm.MainActivity.Companion.NOTIFICATION_ID
import java.util.logging.Logger


class AlarmService : Service() {

    private val logger = Logger.getLogger("adadad")
    private lateinit var vibrator: Vibrator
    private lateinit var ringtone: Ringtone
    override fun onCreate() {
        super.onCreate()
        logger.info("AlarmService onCreate")
        // we will use vibrator first
        vibrator = getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        var alarmUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM)
        if (alarmUri == null) {
            alarmUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        }
        ringtone = RingtoneManager.getRingtone(this, alarmUri)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        logger.info("AlarmService onStartCommand")
        when (intent?.action) {
            MainActivity.ALARM_FIRE -> {
                NotificationManagerCompat.from(this).apply {
                    val channelId = MainActivity.ALARM_CHANNEL_ID
                    val channelName = MainActivity.ALARM_CHANNEL_NAME
                    val notificationChannel =
                        NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_HIGH)
                    createNotificationChannel(notificationChannel)
                }
                val mainIntent = PendingIntent.getActivity(
                    this,
                    0,
                    Intent(this, MainActivity::class.java).apply {
                        putExtra(MainActivity.OPEN_FROM_NOTIFICATION, true)
                    },
                    PendingIntent.FLAG_IMMUTABLE
                )
                val intentDismiss = Intent(MainActivity.ALARM_DISMISS)
                intentDismiss.setClass(this, AlarmReceiver::class.java)
                val pendingDismiss = PendingIntent.getBroadcast(this, 0, intentDismiss, PendingIntent.FLAG_IMMUTABLE)

                val notification = NotificationCompat
                    .Builder(this, MainActivity.ALARM_CHANNEL_ID)
                    .setContentTitle(getString(R.string.alarm_content_title))
                    .setContentText(getString(R.string.alarm_content_text))
                    .setSmallIcon(R.drawable.vector_ic_alarm_24)
                    .setPriority(NotificationCompat.PRIORITY_HIGH)
                    .setCategory(NotificationCompat.CATEGORY_ALARM)
                    .setContentIntent(mainIntent)
                    .addAction(
                        R.drawable.vector_ic_alarm_off_24,
                        getString(R.string.dismiss),
                        pendingDismiss
                    )
                    .setDefaults(Notification.DEFAULT_LIGHTS)
                    .build()

                startForeground(NOTIFICATION_ID, notification)

                vibrator.vibrate(VibrationEffect.createWaveform(longArrayOf(0, 100, 1000), 0))
                ringtone.play()
            }
            MainActivity.ALARM_DISMISS -> {
                vibrator.cancel()
                ringtone.stop()
                stopForeground(true)
            }
        }

        return START_STICKY
    }

    override fun onDestroy() {
        super.onDestroy()
        logger.info("AlarmService onDestroy")
        vibrator.cancel()
        ringtone.stop()
    }

    override fun onBind(intent: Intent?): IBinder? = null
}

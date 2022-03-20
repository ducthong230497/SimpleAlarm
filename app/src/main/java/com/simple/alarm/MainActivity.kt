package com.simple.alarm

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.simple.alarm.databinding.ActivityMainBinding
import java.util.*


class MainActivity : AppCompatActivity() {

    companion object {
        const val NOTIFICATION_ID = 1
        const val ALARM_FIRE = "ALARM_FIRE"
        const val ALARM_DISMISS = "ALARM_DISMISS"
        const val DAILY_INTERVAL = 24 * 60 * 60 * 1000L
        const val OPEN_FROM_NOTIFICATION = "OPEN_FROM_NOTIFICATION"
        const val ALARM_CHANNEL_ID = "ALARM_CHANNEL_ID"
        const val ALARM_CHANNEL_NAME = "ALARM_CHANNEL_NAME"
    }

    private val openFromNotification by lazy { intent.getBooleanExtra(OPEN_FROM_NOTIFICATION, false) }

    private lateinit var binding: ActivityMainBinding

    var pendingIntent: PendingIntent? = null
    private lateinit var alarmManager: AlarmManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)

        if (openFromNotification) {
            sendBroadcast(Intent(this, AlarmReceiver::class.java).apply { action = ALARM_DISMISS })
        }

        alarmManager = getSystemService(ALARM_SERVICE) as AlarmManager
        binding.btnSetAlarm.setOnClickListener {
            Toast.makeText(this@MainActivity, getString(R.string.alarm_set).uppercase(), Toast.LENGTH_SHORT).show()
            val calendar = Calendar.getInstance()
            val calendarTarget = calendar.clone() as Calendar

            calendarTarget.set(Calendar.HOUR_OF_DAY, binding.timePicker.hour)
            calendarTarget.set(Calendar.MINUTE, binding.timePicker.minute)

            if (calendarTarget < calendar) {
                calendarTarget.add(Calendar.DATE, 1)
            }

            val intent = Intent(this, AlarmReceiver::class.java).apply { action = ALARM_FIRE }
            pendingIntent = PendingIntent.getBroadcast(
                baseContext, 0, intent, PendingIntent.FLAG_IMMUTABLE
            )
            val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
            if (binding.checkRepeat.isChecked) {
                alarmManager.setInexactRepeating(AlarmManager.RTC_WAKEUP, calendarTarget.timeInMillis, DAILY_INTERVAL, pendingIntent)
            } else {
                alarmManager.setExact(AlarmManager.RTC_WAKEUP, calendarTarget.timeInMillis, pendingIntent)
            }
        }
    }
}

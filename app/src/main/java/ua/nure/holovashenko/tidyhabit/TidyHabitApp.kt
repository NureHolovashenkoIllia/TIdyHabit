package ua.nure.holovashenko.tidyhabit

import android.app.Application
import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import dagger.hilt.android.HiltAndroidApp
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Intent
import android.util.Log
import ua.nure.holovashenko.tidyhabit.notification.ReminderAlarmReceiver
import java.util.*

@HiltAndroidApp
class TidyHabitApp : Application() {

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate() {
        super.onCreate()
        scheduleDailyAlarm(applicationContext, "daily_tasks", 10, 0)
        scheduleDailyAlarm(applicationContext, "streak_warning", 18, 0)
    }

    private fun scheduleDailyAlarm(context: Context, type: String, hourOfDay: Int, minute: Int) {
        val alarmManager = context.getSystemService(ALARM_SERVICE) as AlarmManager

        val intent = Intent(context, ReminderAlarmReceiver::class.java).apply {
            putExtra("type", type)
        }

        val pendingIntent = PendingIntent.getBroadcast(
            context,
            type.hashCode(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val calendar = Calendar.getInstance().apply {
            timeInMillis = System.currentTimeMillis()
            set(Calendar.HOUR_OF_DAY, hourOfDay)
            set(Calendar.MINUTE, minute)
            set(Calendar.SECOND, 0)
            if (before(Calendar.getInstance())) {
                add(Calendar.DATE, 1)
            }
        }

        try {
            alarmManager.setExactAndAllowWhileIdle(
                AlarmManager.RTC_WAKEUP,
                calendar.timeInMillis,
                pendingIntent
            )
        } catch (e: SecurityException) {
            Log.e("AlarmDebug", "SecurityException while scheduling alarm: ${e.message}")
        }
    }
}

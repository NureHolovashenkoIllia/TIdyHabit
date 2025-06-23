package ua.nure.holovashenko.tidyhabit.notification

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.annotation.RequiresApi
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import ua.nure.holovashenko.tidyhabit.R
import ua.nure.holovashenko.tidyhabit.data.local.repository.TaskRepository
import ua.nure.holovashenko.tidyhabit.data.local.repository.UserRepository
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import javax.inject.Inject

@AndroidEntryPoint
class ReminderAlarmReceiver : BroadcastReceiver() {

    @Inject lateinit var userRepository: UserRepository
    @Inject lateinit var taskRepository: TaskRepository

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onReceive(context: Context, intent: Intent) {
        val type = intent.getStringExtra("type") ?: return

        CoroutineScope(Dispatchers.Default).launch {
            when (type) {
                "daily_tasks" -> {
                    sendDailyTasksNotification(context)
                    AlarmScheduler.schedule(context, "daily_tasks", 10, 0)
                }
                "streak_warning" -> {
                    sendStreakWarningNotification(context)
                    AlarmScheduler.schedule(context, "streak_warning", 18, 0)
                }
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private suspend fun sendDailyTasksNotification(context: Context) {
        userRepository.getCurrentUser() ?: return
        val tasks = taskRepository.getTasks()
        val today = LocalDate.now()
        val todayTasks = tasks.filter {
            val date = Instant.ofEpochMilli(it.dueDate).atZone(ZoneId.systemDefault()).toLocalDate()
            date == today
        }

        when {
            todayTasks.isEmpty() -> NotificationHelper.showNotification(
                context,
                context.getString(R.string.no_tasks_today_title),
                context.getString(R.string.no_tasks_today_message),
                NotificationIds.NO_TASKS
            )
            todayTasks.any { !it.isCompleted } -> {
                val uncompleted = todayTasks.count { !it.isCompleted }
                NotificationHelper.showNotification(
                    context,
                    context.getString(R.string.daily_tasks_title),
                    context.resources.getQuantityString(
                        R.plurals.uncompleted_tasks_message,
                        uncompleted,
                        uncompleted
                    ),
                    NotificationIds.UNCOMPLETED_TASKS
                )
            }
            else -> NotificationHelper.showNotification(
                context,
                context.getString(R.string.all_tasks_done_title),
                context.getString(R.string.all_tasks_done_message),
                NotificationIds.ALL_DONE
            )
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private suspend fun sendStreakWarningNotification(context: Context) {
        val user = userRepository.getCurrentUser() ?: return
        val today = LocalDate.now().toString()
        if (user.lastActiveDate != today) {
            NotificationHelper.showNotification(
                context,
                context.getString(R.string.streak_warning_title),
                context.getString(R.string.streak_warning_message),
                NotificationIds.STREAK_WARNING
            )
        }
    }
}

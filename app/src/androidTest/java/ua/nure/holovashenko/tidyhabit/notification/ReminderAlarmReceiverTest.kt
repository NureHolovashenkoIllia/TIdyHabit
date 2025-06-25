package ua.nure.holovashenko.tidyhabit.notification

import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import io.mockk.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.TestDispatcher
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import ua.nure.holovashenko.tidyhabit.R
import ua.nure.holovashenko.tidyhabit.data.local.model.Task
import ua.nure.holovashenko.tidyhabit.data.local.model.TaskCategory
import ua.nure.holovashenko.tidyhabit.data.local.model.User
import ua.nure.holovashenko.tidyhabit.data.local.repository.TaskRepository
import ua.nure.holovashenko.tidyhabit.data.local.repository.UserRepository
import java.time.LocalDate
import java.time.ZoneId

@HiltAndroidTest
@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
class ReminderAlarmReceiverTest {

    @get:Rule
    val hiltRule = HiltAndroidRule(this)

    private lateinit var context: Context
    private lateinit var testDispatcher: TestDispatcher
    private lateinit var receiver: ReminderAlarmReceiver
    private lateinit var userRepository: UserRepository
    private lateinit var taskRepository: TaskRepository

    private lateinit var user: User
    private lateinit var tasks: List<Task>

    @RequiresApi(Build.VERSION_CODES.O)
    @Before
    fun setUp() {
        hiltRule.inject()

        context = ApplicationProvider.getApplicationContext()
        testDispatcher = UnconfinedTestDispatcher()

        user = User(1, "Illia", 20)
        tasks = listOf(
            Task(
                id = 1,
                userId = 1,
                title = "Title",
                description = "Description",
                category = TaskCategory.OTHER,
                dueDate = LocalDate.now().atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli(),
                isCompleted = false
            )
        )

        userRepository = mockk(relaxed = true)
        taskRepository = mockk(relaxed = true)

        receiver = spyk(ReminderAlarmReceiver(testDispatcher, userRepository, taskRepository))

        mockkObject(NotificationHelper)
        clearMocks(NotificationHelper)
        every { NotificationHelper.showNotification(any(), any(), any(), any()) } just Runs
    }

    @RequiresApi(Build.VERSION_CODES.O)
    @Test
    fun shouldShowNoTasksNotificationWhenThereAreNoTasks() = runTest {
        coEvery { userRepository.getCurrentUser() } returns user
        coEvery { taskRepository.getTasks() } returns emptyList()

        val intent = Intent().apply { putExtra("type", "daily_tasks") }

        receiver.onReceive(context, intent)
        testDispatcher.scheduler.advanceUntilIdle()

        verify {
            NotificationHelper.showNotification(
                context,
                context.getString(R.string.no_tasks_today_title),
                context.getString(R.string.no_tasks_today_message),
                NotificationIds.NO_TASKS
            )
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    @Test
    fun shouldShowUncompletedTasksNotification() = runTest {
        coEvery { userRepository.getCurrentUser() } returns user
        coEvery { taskRepository.getTasks() } returns tasks

        val intent = Intent().apply { putExtra("type", "daily_tasks") }

        receiver.onReceive(context, intent)
        testDispatcher.scheduler.advanceUntilIdle()

        verify {
            NotificationHelper.showNotification(
                context,
                context.getString(R.string.daily_tasks_title),
                context.resources.getQuantityString(
                    R.plurals.uncompleted_tasks_message, 1, 1
                ),
                NotificationIds.UNCOMPLETED_TASKS
            )
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    @Test
    fun shouldShowAllTasksDoneNotification() = runTest {
        val completedTasks = tasks.map { it.copy(isCompleted = true) }

        coEvery { userRepository.getCurrentUser() } returns user
        coEvery { taskRepository.getTasks() } returns completedTasks

        val intent = Intent().apply { putExtra("type", "daily_tasks") }

        receiver.onReceive(context, intent)
        testDispatcher.scheduler.advanceUntilIdle()

        verify {
            NotificationHelper.showNotification(
                context,
                context.getString(R.string.all_tasks_done_title),
                context.getString(R.string.all_tasks_done_message),
                NotificationIds.ALL_DONE
            )
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    @Test
    fun shouldShowStreakWarningNotificationIfUserInactive() = runTest {
        val inactiveUser = user.copy(lastActiveDate = LocalDate.now().minusDays(1).toString())
        coEvery { userRepository.getCurrentUser() } returns inactiveUser

        val intent = Intent().apply { putExtra("type", "streak_warning") }

        receiver.onReceive(context, intent)
        testDispatcher.scheduler.advanceUntilIdle()

        verify {
            NotificationHelper.showNotification(
                context,
                context.getString(R.string.streak_warning_title),
                context.getString(R.string.streak_warning_message),
                NotificationIds.STREAK_WARNING
            )
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    @Test
    fun shouldNotShowStreakWarningIfUserWasActiveToday() = runTest {
        val activeUser = user.copy(lastActiveDate = LocalDate.now().toString())
        coEvery { userRepository.getCurrentUser() } returns activeUser

        val intent = Intent().apply { putExtra("type", "streak_warning") }

        receiver.onReceive(context, intent)
        testDispatcher.scheduler.advanceUntilIdle()

        verify(exactly = 0) {
            NotificationHelper.showNotification(any(), any(), any(), any())
        }
    }
}

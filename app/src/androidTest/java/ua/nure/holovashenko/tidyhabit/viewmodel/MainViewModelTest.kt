package ua.nure.holovashenko.tidyhabit.viewmodel

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.test.ext.junit.runners.AndroidJUnit4
import io.mockk.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import ua.nure.holovashenko.tidyhabit.data.local.model.Task
import ua.nure.holovashenko.tidyhabit.data.local.model.TaskCategory
import ua.nure.holovashenko.tidyhabit.data.local.model.User
import ua.nure.holovashenko.tidyhabit.data.local.preferences.UserPreferences
import ua.nure.holovashenko.tidyhabit.data.local.repository.TaskRepository
import ua.nure.holovashenko.tidyhabit.data.local.repository.UserRepository
import ua.nure.holovashenko.tidyhabit.presentation.main.MainViewModel
import ua.nure.holovashenko.tidyhabit.rules.MainDispatcherRule

@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
class MainViewModelTest {

    @get:Rule
    val dispatcherRule = MainDispatcherRule()

    private lateinit var viewModel: MainViewModel
    private lateinit var taskRepository: TaskRepository
    private lateinit var userRepository: UserRepository
    private lateinit var userPreferences: UserPreferences

    private val testUser = User(id = 1, name = "Illia", age = 25)
    private val testTasks = listOf(
        Task(id = 1, title = "Task1", description = "Desc", userId = 1, category = TaskCategory.OTHER, dueDate = 0),
        Task(id = 2, title = "Task2", description = "Desc", userId = 1, category = TaskCategory.OTHER, dueDate = 0)
    )

    @Before
    fun setup() {
        taskRepository = mockk(relaxed = true)
        userRepository = mockk(relaxed = true)
        userPreferences = mockk(relaxed = true)

        coEvery { userRepository.getCurrentUser() } returns testUser
        coEvery { taskRepository.getTasks() } returns testTasks

        viewModel = MainViewModel(taskRepository, userRepository, userPreferences)
    }

    @Test
    fun refreshAll_shouldLoadUserAndTasks() = runTest {
        viewModel.refreshAll()
        advanceUntilIdle()

        assertEquals(testUser, viewModel.user.first())
        assertEquals(testTasks, viewModel.tasks.first())
    }

    @Test
    fun refreshTasks_shouldUpdateTasksOnly() = runTest {
        val otherTasks = listOf(Task(id = 99, title = "NewTask", description = "test99", userId = 1, category = TaskCategory.OTHER, dueDate = 0))
        coEvery { taskRepository.getTasks() } returns otherTasks

        viewModel.refreshTasks()
        advanceUntilIdle()

        assertEquals(otherTasks, viewModel.tasks.first())
    }

    @RequiresApi(Build.VERSION_CODES.O)
    @Test
    fun completeTask_shouldMarkTaskCompletedAddXpRecordActivityAndRefresh() = runTest {
        val task = testTasks.first()

        viewModel.completeTask(task)

        coVerify { taskRepository.markAsCompleted(task) }
        coVerify { userRepository.addXP(10) }
        coVerify { userRepository.recordDailyActivity() }
        coVerify { userRepository.getCurrentUser() }
        coVerify { taskRepository.getTasks() }
    }

    @Test
    fun deleteTask_shouldDeleteTaskAndRefreshTasks() = runTest {
        val task = testTasks.first()

        viewModel.deleteTask(task)

        coVerify { taskRepository.deleteTask(task) }
        coVerify { taskRepository.getTasks() }
    }

    @Test
    fun logout_shouldClearPreferencesAndCallCallback() = runTest {
        var callbackCalled = false

        viewModel.logout {
            callbackCalled = true
        }

        coVerify { userPreferences.clearUserData() }
        assertTrue(callbackCalled)
    }
}
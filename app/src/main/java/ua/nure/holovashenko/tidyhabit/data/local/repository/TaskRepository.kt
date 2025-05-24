package ua.nure.holovashenko.tidyhabit.data.local.repository

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import ua.nure.holovashenko.tidyhabit.data.local.dao.TaskDao
import ua.nure.holovashenko.tidyhabit.data.local.model.Task
import ua.nure.holovashenko.tidyhabit.data.local.preferences.UserPreferences
import javax.inject.Inject

class TaskRepository @Inject constructor(
    private val taskDao: TaskDao,
    private val userPreferences: UserPreferences
) {
    @OptIn(ExperimentalCoroutinesApi::class)
    val tasks: Flow<List<Task>> = userPreferences.userIdFlow.flatMapLatest { userId ->
        if (userId != null) taskDao.getAllTasksForUser(userId) else flowOf(emptyList())
    }

    suspend fun addTask(task: Task) = taskDao.insertTask(task)

    suspend fun markAsCompleted(task: Task) {
        taskDao.updateTask(task.copy(isCompleted = true))
    }

    suspend fun deleteTask(task: Task) = taskDao.deleteTask(task)
}

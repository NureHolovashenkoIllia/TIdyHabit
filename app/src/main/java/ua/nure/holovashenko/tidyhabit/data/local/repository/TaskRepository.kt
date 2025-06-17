package ua.nure.holovashenko.tidyhabit.data.local.repository

import kotlinx.coroutines.flow.firstOrNull
import ua.nure.holovashenko.tidyhabit.data.local.dao.TaskDao
import ua.nure.holovashenko.tidyhabit.data.local.model.Task
import ua.nure.holovashenko.tidyhabit.data.local.preferences.UserPreferences
import javax.inject.Inject

class TaskRepository @Inject constructor(
    private val taskDao: TaskDao,
    private val userPreferences: UserPreferences
) {
    suspend fun getTasks(): List<Task> {
        val userId = userPreferences.getUserId()
        return if (userId != null) taskDao.getAllTasksForUser(userId).firstOrNull() ?: emptyList()
        else emptyList()
    }

    suspend fun addTask(task: Task) = taskDao.insertTask(task)

    suspend fun markAsCompleted(task: Task) {
        taskDao.updateTask(task.copy(isCompleted = true))
    }

    suspend fun deleteTask(task: Task) = taskDao.deleteTask(task)
}

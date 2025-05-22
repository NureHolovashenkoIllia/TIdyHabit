package ua.nure.holovashenko.tidyhabit.data.local.repository

import kotlinx.coroutines.flow.Flow
import ua.nure.holovashenko.tidyhabit.data.local.dao.TaskDao
import ua.nure.holovashenko.tidyhabit.data.local.model.Task

class TaskRepository(private val taskDao: TaskDao) {
    val tasks: Flow<List<Task>> = taskDao.getAllTasks()

    suspend fun addTask(task: Task) = taskDao.insertTask(task)

    suspend fun markAsCompleted(task: Task) {
        taskDao.updateTask(task.copy(isCompleted = true))
    }

    suspend fun deleteTask(task: Task) = taskDao.deleteTask(task)
}

package ua.nure.holovashenko.tidyhabit.presentation.main

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import ua.nure.holovashenko.tidyhabit.data.local.model.Task
import ua.nure.holovashenko.tidyhabit.data.local.model.User
import ua.nure.holovashenko.tidyhabit.data.local.preferences.UserPreferences
import ua.nure.holovashenko.tidyhabit.data.local.repository.TaskRepository
import ua.nure.holovashenko.tidyhabit.data.local.repository.UserRepository
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val taskRepo: TaskRepository,
    private val userRepo: UserRepository,
    private val userPreferences: UserPreferences
) : ViewModel() {

    private val _tasks = MutableStateFlow<List<Task>>(emptyList())
    val tasks: StateFlow<List<Task>> get() = _tasks

    private val _user = MutableStateFlow<User?>(null)
    val user: StateFlow<User?> get() = _user

    init {
        refreshAll()
    }

    fun refreshAll() = viewModelScope.launch {
        _user.value = userRepo.getCurrentUser()
        _tasks.value = taskRepo.getTasks()
    }

    fun refreshTasks() = viewModelScope.launch {
        _tasks.value = taskRepo.getTasks()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun completeTask(task: Task) {
        viewModelScope.launch {
            taskRepo.markAsCompleted(task)
            userRepo.addXP(10)
            userRepo.recordDailyActivity()
            refreshAll()
        }
    }

    fun deleteTask(task: Task) {
        viewModelScope.launch {
            taskRepo.deleteTask(task)
            refreshTasks()
        }
    }

    fun logout(onLoggedOut: () -> Unit) {
        viewModelScope.launch {
            userPreferences.clearUserData()
            onLoggedOut()
        }
    }
}


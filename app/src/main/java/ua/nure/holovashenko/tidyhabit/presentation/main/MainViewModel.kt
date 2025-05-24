package ua.nure.holovashenko.tidyhabit.presentation.main

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
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

    val tasks: StateFlow<List<Task>> = taskRepo.tasks.stateIn(
        viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList()
    )

    val user: StateFlow<User?> = userRepo.currentUser.stateIn(
        viewModelScope, SharingStarted.WhileSubscribed(5000), null
    )

    @RequiresApi(Build.VERSION_CODES.O)
    fun completeTask(task: Task) {
        viewModelScope.launch {
            taskRepo.markAsCompleted(task)
            userRepo.addXP(10)
            userRepo.recordDailyActivity()
        }
    }

    fun deleteTask(task: Task) {
        viewModelScope.launch {
            taskRepo.deleteTask(task)
        }
    }

    fun logout(onLoggedOut: () -> Unit) = viewModelScope.launch {
        userPreferences.clearUserData()
        onLoggedOut()
    }
}


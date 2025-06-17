package ua.nure.holovashenko.tidyhabit.presentation.create

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import ua.nure.holovashenko.tidyhabit.data.local.model.Task
import ua.nure.holovashenko.tidyhabit.data.local.model.TaskCategory
import ua.nure.holovashenko.tidyhabit.data.local.repository.TaskRepository
import ua.nure.holovashenko.tidyhabit.data.local.repository.UserRepository
import javax.inject.Inject

@HiltViewModel
class CreateTaskViewModel @Inject constructor(
    private val repository: TaskRepository,
    private val userRepository: UserRepository
) : ViewModel() {

    var title by mutableStateOf("")
    var description by mutableStateOf("")
    var category by mutableStateOf(TaskCategory.OTHER)
    var dueDateMillis by mutableLongStateOf(System.currentTimeMillis())

    fun saveTask(onSaved: () -> Unit) {
        viewModelScope.launch {
            val currentUser = userRepository.getCurrentUser()
            val userId = currentUser?.id

            if (userId != null) {
                val task = Task(
                    title = title.trim(),
                    description = description.trim(),
                    category = category,
                    dueDate = dueDateMillis,
                    isCompleted = false,
                    userId = userId
                )
                repository.addTask(task)
                onSaved()
            }
        }
    }
}

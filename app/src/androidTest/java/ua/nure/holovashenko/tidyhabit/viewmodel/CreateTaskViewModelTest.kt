package ua.nure.holovashenko.tidyhabit.viewmodel

import io.mockk.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import ua.nure.holovashenko.tidyhabit.data.local.model.Task
import ua.nure.holovashenko.tidyhabit.data.local.model.TaskCategory
import ua.nure.holovashenko.tidyhabit.data.local.model.User
import ua.nure.holovashenko.tidyhabit.data.local.repository.TaskRepository
import ua.nure.holovashenko.tidyhabit.data.local.repository.UserRepository
import ua.nure.holovashenko.tidyhabit.presentation.create.CreateTaskViewModel

@OptIn(ExperimentalCoroutinesApi::class)
class CreateTaskViewModelTest {

    private lateinit var viewModel: CreateTaskViewModel
    private lateinit var taskRepository: TaskRepository
    private lateinit var userRepository: UserRepository

    private val testUser = User(id = 1, name = "Illia", age = 20)

    @Before
    fun setUp() {
        taskRepository = mockk(relaxed = true)
        userRepository = mockk()
        viewModel = CreateTaskViewModel(taskRepository, userRepository)
    }

    @Test
    fun saveTask_callsAddTaskAndOnSaved_whenUserExists() = runTest {
        coEvery { userRepository.getCurrentUser() } returns testUser

        viewModel.title = " Test title "
        viewModel.description = " Test desc "
        viewModel.category = TaskCategory.LAUNDRY
        viewModel.dueDateMillis = 123456789L

        var onSavedCalled = false
        viewModel.saveTask {
            onSavedCalled = true
        }

        coVerify {
            taskRepository.addTask(
                Task(
                    title = "Test title",
                    description = "Test desc",
                    category = TaskCategory.LAUNDRY,
                    dueDate = 123456789L,
                    isCompleted = false,
                    userId = testUser.id
                )
            )
        }

        Assert.assertTrue(onSavedCalled)
    }

    @Test
    fun saveTask_doesNotCallAddTaskOrOnSaved_whenUserIsNull() = runTest {
        coEvery { userRepository.getCurrentUser() } returns null

        var onSavedCalled = false
        viewModel.saveTask {
            onSavedCalled = true
        }

        coVerify(exactly = 0) { taskRepository.addTask(any()) }
        Assert.assertFalse(onSavedCalled)
    }
}

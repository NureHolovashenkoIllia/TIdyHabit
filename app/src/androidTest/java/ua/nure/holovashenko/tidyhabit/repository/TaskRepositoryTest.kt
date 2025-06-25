package ua.nure.holovashenko.tidyhabit.repository

import android.content.Context
import android.content.SharedPreferences
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.*
import org.junit.runner.RunWith
import ua.nure.holovashenko.tidyhabit.data.local.dao.TaskDao
import ua.nure.holovashenko.tidyhabit.data.local.dao.UserDao
import ua.nure.holovashenko.tidyhabit.data.local.db.AppDatabase
import ua.nure.holovashenko.tidyhabit.data.local.model.Task
import ua.nure.holovashenko.tidyhabit.data.local.model.TaskCategory
import ua.nure.holovashenko.tidyhabit.data.local.model.User
import ua.nure.holovashenko.tidyhabit.data.local.preferences.UserPreferences
import ua.nure.holovashenko.tidyhabit.data.local.repository.TaskRepository

@HiltAndroidTest
@RunWith(AndroidJUnit4::class)
@OptIn(ExperimentalCoroutinesApi::class)
class TaskRepositoryTest {

    @get:Rule
    val hiltRule = HiltAndroidRule(this)

    private lateinit var db: AppDatabase
    private lateinit var taskDao: TaskDao
    private lateinit var userDao: UserDao
    private lateinit var sharedPrefs: SharedPreferences
    private lateinit var userPrefs: UserPreferences
    private lateinit var repository: TaskRepository

    @Before
    fun setup() {
        hiltRule.inject()

        val context = ApplicationProvider.getApplicationContext<Context>()

        db = Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java)
            .allowMainThreadQueries()
            .build()

        taskDao = db.taskDao()
        userDao = db.userDao()

        sharedPrefs = context.getSharedPreferences("task_test_prefs", Context.MODE_PRIVATE)
        userPrefs = UserPreferences(sharedPrefs)

        repository = TaskRepository(taskDao, userPrefs)

        val user = User(id = 1, name = "TaskUser", age = 22)
        runTest { userDao.insert(user) }
        userPrefs.saveUser(id = 1, name = "TaskUser", age = 22)
    }

    @After
    fun tearDown() {
        db.close()
        sharedPrefs.edit().clear().commit()
    }

    @Test
    fun getTasks_returnsAllTasksForCurrentUser() = runTest {
        val task1 = Task(userId = 1, title = "Do dishes", category = TaskCategory.DISHES, dueDate = 1000L)
        val task2 = Task(userId = 1, title = "Vacuum room", category = TaskCategory.VACUUM, dueDate = 2000L)
        taskDao.insertTask(task1)
        taskDao.insertTask(task2)

        val result = repository.getTasks()

        Assert.assertEquals(2, result.size)
        Assert.assertTrue(result.any { it.title == "Do dishes" })
    }

    @Test
    fun addTask_insertsTaskSuccessfully() = runTest {
        val task = Task(userId = 1, title = "Test laundry", category = TaskCategory.LAUNDRY, dueDate = 3000L)

        repository.addTask(task)

        val tasks = repository.getTasks()
        Assert.assertTrue(tasks.any { it.title == "Test laundry" })
    }

    @Test
    fun markAsCompleted_updatesTaskStatus() = runTest {
        val task = Task(userId = 1, title = "Read book", category = TaskCategory.OTHER, dueDate = 4000L)
        taskDao.insertTask(task)

        val inserted = repository.getTasks().first()
        repository.markAsCompleted(inserted)

        val updated = repository.getTasks().first { it.id == inserted.id }
        Assert.assertTrue(updated.isCompleted)
    }

    @Test
    fun deleteTask_removesTaskSuccessfully() = runTest {
        val task = Task(userId = 1, title = "Programming", category = TaskCategory.OTHER, dueDate = 5000L)
        taskDao.insertTask(task)

        val inserted = repository.getTasks().first()
        repository.deleteTask(inserted)

        val tasks = repository.getTasks()
        Assert.assertFalse(tasks.any { it.id == inserted.id })
    }
}

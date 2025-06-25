package ua.nure.holovashenko.tidyhabit.repository

import android.content.Context
import android.content.SharedPreferences
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import ua.nure.holovashenko.tidyhabit.data.local.dao.UserDao
import ua.nure.holovashenko.tidyhabit.data.local.db.AppDatabase
import ua.nure.holovashenko.tidyhabit.data.local.model.User
import ua.nure.holovashenko.tidyhabit.data.local.preferences.UserPreferences
import ua.nure.holovashenko.tidyhabit.data.local.repository.UserRepository
import java.time.LocalDate

@HiltAndroidTest
@RunWith(AndroidJUnit4::class)
@OptIn(ExperimentalCoroutinesApi::class)
class UserRepositoryTest {

    @get:Rule
    val hiltRule = HiltAndroidRule(this)

    private lateinit var db: AppDatabase
    private lateinit var userDao: UserDao
    private lateinit var sharedPrefs: SharedPreferences
    private lateinit var userPrefs: UserPreferences
    private lateinit var repository: UserRepository

    @Before
    fun setup() {
        hiltRule.inject()

        val context = ApplicationProvider.getApplicationContext<Context>()

        db = Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java)
            .allowMainThreadQueries()
            .build()
        userDao = db.userDao()

        sharedPrefs = context.getSharedPreferences("test_prefs", Context.MODE_PRIVATE)
        userPrefs = UserPreferences(sharedPrefs)

        repository = UserRepository(userDao, userPrefs)
    }

    @After
    fun tearDown() {
        db.close()
        sharedPrefs.edit().clear().commit()
    }

    @Test
    fun getCurrentUser_returnsCorrectUser() = runTest {
        val user = User(id = 1, name = "Illia", age = 20)
        userDao.insert(user)
        userPrefs.saveUser(id = 1, name = "Illia", age = 20)

        val result = repository.getCurrentUser()

        assertNotNull(result)
        assertEquals(user.name, result?.name)
        assertEquals(user.age, result?.age)
    }

    @Test
    fun addXP_increasesXP_andLevelsUp() = runTest {
        val user = User(id = 2, name = "Anton", age = 20, xp = 90, level = 1)
        userDao.insert(user)
        userPrefs.saveUser(id = 2, name = "Anton", age = 20)

        repository.addXP(15)

        val updated = userDao.getUserById(2).first()
        assertEquals(1 + 1, updated?.level)
        assertEquals(5, updated?.xp)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    @Test
    fun recordDailyActivity_updatesStreakCorrectly() = runTest {
        val yesterday = LocalDate.now().minusDays(1).toString()
        val user = User(id = 3, name = "Anatoliy", age = 20, streak = 3, lastActiveDate = yesterday)
        userDao.insert(user)
        userPrefs.saveUser(id = 3, name = "Anatoliy", age = 20)

        repository.recordDailyActivity()

        val updated = userDao.getUserById(3).first()
        assertEquals(4, updated?.streak)
        assertEquals(LocalDate.now().toString(), updated?.lastActiveDate)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    @Test
    fun recordDailyActivity_resetsStreakIfInactive() = runTest {
        val user = User(id = 4, name = "Misha", age = 21, streak = 7, lastActiveDate = "2022-01-01")
        userDao.insert(user)
        userPrefs.saveUser(id = 4, name = "Misha", age = 21)

        repository.recordDailyActivity()

        val updated = userDao.getUserById(4).first()
        assertEquals(1, updated?.streak)
        assertEquals(LocalDate.now().toString(), updated?.lastActiveDate)
    }
}
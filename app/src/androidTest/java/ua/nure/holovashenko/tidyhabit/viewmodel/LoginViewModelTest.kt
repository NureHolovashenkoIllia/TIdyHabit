package ua.nure.holovashenko.tidyhabit.viewmodel

import android.content.Context
import android.content.SharedPreferences
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.*
import org.junit.runner.RunWith
import ua.nure.holovashenko.tidyhabit.data.local.db.AppDatabase
import ua.nure.holovashenko.tidyhabit.data.local.model.User
import ua.nure.holovashenko.tidyhabit.data.local.preferences.UserPreferences
import ua.nure.holovashenko.tidyhabit.presentation.login.LoginViewModel

@HiltAndroidTest
@RunWith(AndroidJUnit4::class)
@OptIn(ExperimentalCoroutinesApi::class)
class LoginViewModelTest {

    @get:Rule
    val hiltRule = HiltAndroidRule(this)

    private lateinit var db: AppDatabase
    private lateinit var viewModel: LoginViewModel
    private lateinit var userPrefs: UserPreferences

    private val testUser = User(id = 1, name = "Illia", age = 20)

    @Before
    fun setup() {
        hiltRule.inject()

        val context = ApplicationProvider.getApplicationContext<Context>()

        db = Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java)
            .allowMainThreadQueries()
            .build()

        val sharedPrefs: SharedPreferences = context.getSharedPreferences("test_prefs", Context.MODE_PRIVATE)
        userPrefs = UserPreferences(sharedPrefs)

        viewModel = LoginViewModel(db, userPrefs)
    }

    @After
    fun teardown() {
        db.close()
    }

    @Test
    fun checkAutoLogin_returnsTrue_ifUserExists() = runTest {
        userPrefs.saveUser(1, "Illia", 20)

        viewModel.checkAutoLogin()
        viewModel.loginSuccess.first { it }
        
        Assert.assertTrue(viewModel.loginSuccess.value)
        Assert.assertTrue(viewModel.isLoginChecked.value)
    }

    @Test
    fun checkAutoLogin_returnsFalse_ifNoUserSaved() = runTest {
        viewModel.checkAutoLogin()
        viewModel.isLoginChecked.first { it }

        Assert.assertFalse(viewModel.loginSuccess.value)
        Assert.assertTrue(viewModel.isLoginChecked.value)
    }

    @Test
    fun login_insertsNewUser_ifNotExists() = runTest {
        viewModel.login("Dmytro", 22)
        viewModel.loginSuccess.first { it }

        val userFromDb = db.userDao().getUserByName("Dmytro")
        Assert.assertNotNull(userFromDb)
        Assert.assertEquals("Dmytro", userFromDb?.name)
    }

    @Test
    fun login_usesExistingUser_ifFound() = runTest {
        db.userDao().insert(testUser)

        viewModel.login("Illia", 20)
        viewModel.loginSuccess.first { it }

        Assert.assertTrue(viewModel.loginSuccess.value)
    }

    @Test
    fun login_savesUserToPreferences() = runTest {
        viewModel.login("Petro", 23)
        viewModel.loginSuccess.first { it }

        val savedUser = userPrefs.getUser()
        Assert.assertEquals("Petro", savedUser?.first)
        Assert.assertEquals(23, savedUser?.second)
    }
}


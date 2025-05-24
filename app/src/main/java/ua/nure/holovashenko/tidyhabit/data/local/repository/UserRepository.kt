package ua.nure.holovashenko.tidyhabit.data.local.repository

import android.os.Build
import androidx.annotation.RequiresApi
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import ua.nure.holovashenko.tidyhabit.data.local.dao.UserDao
import ua.nure.holovashenko.tidyhabit.data.local.model.User
import ua.nure.holovashenko.tidyhabit.data.local.preferences.UserPreferences
import java.time.LocalDate
import javax.inject.Inject

class UserRepository @Inject constructor(
    private val userDao: UserDao,
    private val userPrefs: UserPreferences
) {
    @OptIn(ExperimentalCoroutinesApi::class)
    val currentUser: Flow<User?> = userPrefs.userIdFlow.flatMapLatest { id ->
        if (id != null) userDao.getUserById(id) else flowOf(null)
    }

    suspend fun addXP(amount: Int) {
        val userId = userPrefs.userIdFlow.firstOrNull()
        if (userId != null) {
            val user = userDao.getUserById(userId).firstOrNull()
            if (user != null) {
                val totalXP = user.xp + amount
                val newLevel = if (totalXP >= 100) user.level + 1 else user.level
                val xpForLevel = totalXP % 100

                userDao.updateUser(user.copy(xp = xpForLevel, level = newLevel))
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    suspend fun recordDailyActivity() {
        val userId = userPrefs.userIdFlow.firstOrNull()
        if (userId != null) {
            val user = userDao.getUserById(userId).firstOrNull()
            if (user != null) {
                val today = LocalDate.now().toString()
                val updatedUser = when (user.lastActiveDate) {
                    today -> user
                    LocalDate.now().minusDays(1).toString() -> user.copy(
                        streak = user.streak + 1,
                        lastActiveDate = today
                    )
                    else -> user.copy(
                        streak = 1,
                        lastActiveDate = today
                    )
                }
                userDao.updateUser(updatedUser)
            }
        }
    }
}

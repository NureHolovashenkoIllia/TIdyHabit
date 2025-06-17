package ua.nure.holovashenko.tidyhabit.data.local.preferences

import android.content.SharedPreferences

class UserPreferences(
    private val prefs: SharedPreferences
) {
    companion object {
        private const val USER_ID = "user_id"
        private const val USER_NAME = "user_name"
        private const val USER_AGE = "user_age"
    }

    fun saveUser(id: Int, name: String, age: Int) {
        prefs.edit().apply {
            putInt(USER_ID, id)
            putString(USER_NAME, name)
            putInt(USER_AGE, age)
            apply()
        }
    }

    fun getUser(): Pair<String, Int>? {
        val name = prefs.getString(USER_NAME, null)
        val age = prefs.getInt(USER_AGE, -1)
        return if (name != null && age >= 0) Pair(name, age) else null
    }

    fun getUserId(): Int? {
        val id = prefs.getInt(USER_ID, -1)
        return if (id >= 0) id else null
    }

    fun clearUserData() {
        prefs.edit().clear().apply()
    }
}

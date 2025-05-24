package ua.nure.holovashenko.tidyhabit.data.local.preferences

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class UserPreferences(
    private val dataStore: DataStore<Preferences>
) {
    companion object {
        val USER_NAME = stringPreferencesKey("user_name")
        val USER_AGE = intPreferencesKey("user_age")
        val USER_ID = intPreferencesKey("user_id") // новий ключ
    }

    suspend fun saveUser(id: Int, name: String, age: Int) {
        dataStore.edit {
            it[USER_ID] = id
            it[USER_NAME] = name
            it[USER_AGE] = age
        }
    }

    val userIdFlow: Flow<Int?> = dataStore.data.map { prefs ->
        prefs[USER_ID]
    }

    suspend fun clearUserData() {
        dataStore.edit { it.clear() }
    }

    val userData: Flow<Pair<String, Int>?> = dataStore.data
        .map { prefs ->
            val name = prefs[USER_NAME]
            val age = prefs[USER_AGE]
            if (name != null && age != null) Pair(name, age) else null
        }
}

package ua.nure.holovashenko.tidyhabit.presentation.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import ua.nure.holovashenko.tidyhabit.data.local.db.AppDatabase
import ua.nure.holovashenko.tidyhabit.data.local.model.User
import ua.nure.holovashenko.tidyhabit.data.local.preferences.UserPreferences
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val db: AppDatabase,
    private val userPreferences: UserPreferences
) : ViewModel() {

    private val _loginComplete = MutableStateFlow(false)
    val loginComplete: StateFlow<Boolean> = _loginComplete

    private val _isLoginChecked = MutableStateFlow(false)
    val isLoginChecked: StateFlow<Boolean> = _isLoginChecked

    val userName = MutableStateFlow("")
    val userAge = MutableStateFlow(0)

    fun checkAutoLogin() {
        viewModelScope.launch {
            val user = userPreferences.getUser()
            if (user != null) {
                userName.value = user.first
                userAge.value = user.second
                _loginComplete.value = true
            } else {
                _loginComplete.value = false
            }
            _isLoginChecked.value = true
        }
    }

    fun login(name: String, age: Int) {
        viewModelScope.launch {
            val existingUser = db.userDao().getUserByName(name)
            val userId = if (existingUser != null) {
                existingUser.id
            } else {
                db.userDao().insert(User(name = name, age = age)).toInt()
            }
            userPreferences.saveUser(userId, name, age)
            _loginComplete.value = true
        }
    }
}

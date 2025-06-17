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
    private val userPrefs: UserPreferences
) : ViewModel() {

    private val _isLoginChecked = MutableStateFlow(false)
    val isLoginChecked: StateFlow<Boolean> = _isLoginChecked

    private val _loginSuccess = MutableStateFlow(false)
    val loginSuccess: StateFlow<Boolean> = _loginSuccess

    fun checkAutoLogin() {
        viewModelScope.launch {
            val user = userPrefs.getUser()
            _loginSuccess.value = user != null
            _isLoginChecked.value = true
        }
    }

    fun login(name: String, age: Int) {
        viewModelScope.launch {
            val userId = db.userDao().getUserByName(name)?.id
                ?: db.userDao().insert(User(name = name, age = age)).toInt()

            userPrefs.saveUser(userId, name, age)
            _loginSuccess.value = true
        }
    }
}

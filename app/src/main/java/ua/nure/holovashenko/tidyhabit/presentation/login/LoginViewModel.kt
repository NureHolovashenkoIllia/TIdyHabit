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
    private val prefs: UserPreferences
) : ViewModel() {

    private val _loginComplete = MutableStateFlow(false)
    val loginComplete: StateFlow<Boolean> = _loginComplete

    init {
        viewModelScope.launch {
            prefs.userData.collect { user ->
                if (user != null) _loginComplete.value = true
            }
        }
    }

    fun login(name: String, age: Int) {
        viewModelScope.launch {
            val user = User(name = name, age = age)
            val userId = db.userDao().insert(user)
            prefs.saveUser(name, age)
            _loginComplete.value = true
        }
    }
}

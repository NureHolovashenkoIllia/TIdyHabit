package ua.nure.holovashenko.tidyhabit.presentation.splash

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.compose.runtime.getValue
import ua.nure.holovashenko.tidyhabit.presentation.login.LoginViewModel

@Composable
fun SplashScreen(
    onUserAuthenticated: () -> Unit,
    onLoginRequired: () -> Unit,
    viewModel: LoginViewModel = hiltViewModel()
) {
    LaunchedEffect(Unit) {
        viewModel.checkAutoLogin()
    }

    val isChecked by viewModel.isLoginChecked.collectAsState()
    val isLoggedIn by viewModel.loginSuccess.collectAsState()

    LaunchedEffect(isChecked, isLoggedIn) {
        if (isChecked) {
            if (isLoggedIn) onUserAuthenticated() else onLoginRequired()
        }
    }

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator()
    }
}

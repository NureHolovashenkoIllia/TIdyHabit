package ua.nure.holovashenko.tidyhabit.navigation

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import kotlinx.coroutines.delay
import ua.nure.holovashenko.tidyhabit.presentation.create.CreateTaskScreen
import ua.nure.holovashenko.tidyhabit.presentation.login.LoginScreen
import ua.nure.holovashenko.tidyhabit.presentation.main.MainScreen
import ua.nure.holovashenko.tidyhabit.presentation.splash.SplashScreen

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun AppNavGraph(navController: NavHostController) {
    NavHost(navController = navController, startDestination = NavDestinations.SPLASH) {
        composable(NavDestinations.SPLASH) {
            SplashScreen(
                onLoginRequired = {
                    navController.navigate(NavDestinations.LOGIN) {
                        popUpTo(NavDestinations.SPLASH) { inclusive = true }
                    }
                },
                onUserAuthenticated = {
                    navController.navigate(NavDestinations.MAIN) {
                        popUpTo(NavDestinations.SPLASH) { inclusive = true }
                    }
                }
            )
        }

        composable(NavDestinations.LOGIN) {
            LoginScreen(
                onLoginSuccess = {
                    navController.navigate(NavDestinations.MAIN) {
                        popUpTo(NavDestinations.LOGIN) { inclusive = true }
                    }
                }
            )
        }

        composable(NavDestinations.MAIN) {
            var shouldLogout by remember { mutableStateOf(false) }

            if (shouldLogout) {
                LaunchedEffect(Unit) {
                    delay(1000)
                    navController.popBackStack()
                }
            }

            MainScreen(
                onCreateTaskClick = { navController.navigate(NavDestinations.CREATE_TASK) },
                onLogout = {
                    navController.navigate(NavDestinations.LOGIN) {
                        popUpTo(NavDestinations.MAIN) { inclusive = true }
                        launchSingleTop = true
                    }
                    shouldLogout = true
                }
            )
        }
        composable(NavDestinations.CREATE_TASK) {
            CreateTaskScreen(
                onTaskSaved = { navController.popBackStack() },
                onCancel = { navController.popBackStack() }
            )
        }
    }
}

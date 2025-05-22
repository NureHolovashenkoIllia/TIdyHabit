package ua.nure.holovashenko.tidyhabit.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import ua.nure.holovashenko.tidyhabit.presentation.create.CreateTaskScreen
import ua.nure.holovashenko.tidyhabit.presentation.login.LoginScreen
import ua.nure.holovashenko.tidyhabit.presentation.main.MainScreen

@Composable
fun AppNavGraph(navController: NavHostController) {
    NavHost(navController = navController, startDestination = NavDestinations.LOGIN) {
        composable(NavDestinations.LOGIN) {
            LoginScreen(
                onLoginSuccess = { navController.navigate(NavDestinations.MAIN) }
            )
        }
        composable(NavDestinations.MAIN) {
            MainScreen(
                onCreateTaskClick = { navController.navigate(NavDestinations.CREATE_TASK) }
            )
        }
        composable(NavDestinations.CREATE_TASK) {
            CreateTaskScreen(
                onTaskSaved = { navController.popBackStack() }
            )
        }
    }
}

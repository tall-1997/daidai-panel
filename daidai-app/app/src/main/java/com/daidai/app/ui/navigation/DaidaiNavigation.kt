package com.daidai.app.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.daidai.app.ui.screen.login.LoginScreen
import com.daidai.app.ui.screen.home.HomeScreen
import com.daidai.app.ui.screen.task.TaskDetailScreen
import com.daidai.app.ui.screen.webhelper.WebHelperScreen

sealed class Screen(val route: String) {
    object Login : Screen("login")
    object Home : Screen("home")
    object WebHelper : Screen("webhelper")
    object TaskDetail : Screen("task/{taskId}") {
        fun createRoute(taskId: Int) = "task/$taskId"
    }
}

@Composable
fun DaidaiNavigation(
    navController: NavHostController = rememberNavController()
) {
    NavHost(
        navController = navController,
        startDestination = Screen.Login.route
    ) {
        composable(Screen.Login.route) {
            LoginScreen(
                onLoginSuccess = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Login.route) { inclusive = true }
                    }
                }
            )
        }
        composable(Screen.Home.route) {
            HomeScreen(
                onNavigateToWebHelper = {
                    navController.navigate(Screen.WebHelper.route)
                },
                onNavigateToTaskDetail = { taskId ->
                    navController.navigate(Screen.TaskDetail.createRoute(taskId))
                },
                onLogout = {
                    navController.navigate(Screen.Login.route) {
                        popUpTo(Screen.Home.route) { inclusive = true }
                    }
                }
            )
        }
        composable(
            route = Screen.TaskDetail.route,
            arguments = listOf(navArgument("taskId") { type = NavType.IntType })
        ) { backStackEntry ->
            val taskId = backStackEntry.arguments?.getInt("taskId") ?: return@composable
            TaskDetailScreen(
                taskId = taskId,
                onBack = {
                    navController.popBackStack()
                },
                onEdit = { /* TODO: 导航到编辑页面 */ }
            )
        }
        composable(Screen.WebHelper.route) {
            WebHelperScreen(
                onBack = {
                    navController.popBackStack()
                }
            )
        }
    }
}

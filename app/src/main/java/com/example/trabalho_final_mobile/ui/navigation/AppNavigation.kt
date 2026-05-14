package com.example.trabalho_final_mobile.ui.navigation

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.trabalho_final_mobile.ui.screens.ForgotPasswordScreen
import com.example.trabalho_final_mobile.ui.screens.LoginScreen
import com.example.trabalho_final_mobile.ui.screens.MenuScreen
import com.example.trabalho_final_mobile.ui.screens.RegisterScreen
import com.example.trabalho_final_mobile.ui.viewmodel.AuthViewModel
import com.example.trabalho_final_mobile.ui.viewmodel.TripViewModel

sealed class Routes(val route: String) {
    object Login : Routes("login")
    object Register : Routes("register")
    object ForgotPassword : Routes("forgot_password")
    object Menu : Routes("menu/{email}") {
        fun createRoute(email: String) = "menu/$email"
    }
}

@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    val authViewModel: AuthViewModel = viewModel()
    val tripViewModel: TripViewModel = viewModel()

    NavHost(
        navController = navController,
        startDestination = Routes.Login.route
    ) {
        composable(Routes.Login.route) {
            LoginScreen(
                viewModel = authViewModel,
                onLoginSuccess = { email, _ ->
                    navController.navigate(Routes.Menu.createRoute(email)) {
                        popUpTo(Routes.Login.route) { inclusive = true }
                    }
                },
                onNavigateToRegister = {
                    navController.navigate(Routes.Register.route)
                },
                onNavigateToForgotPassword = {
                    navController.navigate(Routes.ForgotPassword.route)
                }
            )
        }

        composable(Routes.Register.route) {
            RegisterScreen(
                viewModel = authViewModel,
                onRegisterSuccess = {
                    navController.navigate(Routes.Login.route) {
                        popUpTo(Routes.Register.route) { inclusive = true }
                    }
                }
            )
        }

        composable(Routes.ForgotPassword.route) {
            ForgotPasswordScreen(
                viewModel = authViewModel,
                onBackToLogin = {
                    navController.navigate(Routes.Login.route) {
                        popUpTo(Routes.ForgotPassword.route) { inclusive = true }
                    }
                }
            )
        }

        composable(
            route = Routes.Menu.route,
            arguments = listOf(
                navArgument("email") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val email = backStackEntry.arguments?.getString("email") ?: ""
            MenuScreen(
                email = email,
                tripViewModel = tripViewModel
            )
        }
    }
}
package com.example.traveler.controllers


import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.traveler.ui.screens.LoginScreen
import com.example.traveler.ui.screens.SignUpScreen
import com.example.traveler.ui.screens.HomeScreen
import com.example.traveler.ui.screens.LoadingScreen
import com.example.traveler.ui.screens.ProfileSetupScreen
import com.example.traveler.viewmodel.AuthViewModel
import com.example.traveler.viewmodel.UserProfileViewModel


sealed class Screen(val route: String) {
    object Loading : Screen("loading")
    object SignUp : Screen("signup")
    object Login : Screen("login")
    object Home : Screen("home")
    object ProfileSetup : Screen("profile_setup")
}


@Composable
fun Navigation(
    modifier: Modifier = Modifier,
    navController: NavHostController,
    authViewModel: AuthViewModel,
    userProfileViewModel: UserProfileViewModel = viewModel()
) {
    NavHost(
        navController = navController,
        startDestination = Screen.Loading.route,
        modifier = modifier
    ) {
        composable(Screen.Loading.route) {
            // Add a composable for the Loading screen
            // You'll need to manage the state here and navigate to the next screen when ready
            LoadingScreen(navController = navController)
        }
        composable(Screen.SignUp.route) {
            SignUpScreen(
                modifier = modifier,
                navController = navController,
                authViewModel = authViewModel
            )
        }
        composable(Screen.Login.route) {
            LoginScreen(
                modifier = modifier,
                navController = navController,
                authViewModel = authViewModel
            )
        }
        composable(Screen.Home.route) {
            HomeScreen(
                modifier = modifier,
                navController = navController,
                authViewModel = authViewModel
            )
        }
        // **ADDED**: Composable for the ProfileSetupScreen
        composable(Screen.ProfileSetup.route) {
            ProfileSetupScreen(
                modifier = modifier,
                navController = navController,
                userProfileViewModel = userProfileViewModel
            )
        }
    }
}

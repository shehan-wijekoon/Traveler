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
import com.example.traveler.ui.screens.UserProfileScreen
import com.example.traveler.viewmodel.AuthViewModel
import com.example.traveler.viewmodel.UserProfileViewModel
import com.example.traveler.ui.screens.UploadPostScreen
import com.example.traveler.ui.screens.ContentScreen
import com.example.traveler.viewmodel.UploadPostViewModel
import androidx.navigation.NavType
import androidx.navigation.navArgument
import com.example.traveler.viewmodel.HomeViewModel
import com.example.traveler.ui.screens.TravelersGuideScreen
import com.example.traveler.viewmodel.ContentViewModel
import com.example.traveler.viewmodel.ContentViewModelFactory
import com.example.traveler.viewmodel.TravelersGuideViewModel
import com.example.traveler.ui.screens.HashtagSearchScreen


sealed class Screen(val route: String) {
    object Loading : Screen("loading")
    object Login : Screen("login")
    object SignUp : Screen("signup")
    object Home : Screen("home")
    object ProfileSetup : Screen("profile_setup")
    object Content : Screen("content/{postId}") {
        fun createRoute(postId: String) = "content/$postId"
    }
    object UserProfile : Screen("user_profile")
    object UploadPost : Screen("upload_post")
    object TravelersGuide : Screen("travelers_guide")
    object HashtagSearch : Screen("hashtag_search/{tag}") {
        fun createRoute(tag: String) = "hashtag_search/$tag"
    }
}


@Composable
fun Navigation(
    modifier: Modifier = Modifier,
    navController: NavHostController,
    authViewModel: AuthViewModel
) {

    val uploadPostViewModel: UploadPostViewModel = viewModel()
    val homeViewModel: HomeViewModel = viewModel()
    val travelersGuideViewModel: TravelersGuideViewModel = viewModel()

    NavHost(
        navController = navController,
        startDestination = Screen.Loading.route,
        modifier = modifier
    ) {
        composable(Screen.Loading.route) {
            LoadingScreen(navController = navController)
        }

        composable(Screen.Login.route) {
            LoginScreen(
                modifier = modifier,
                navController = navController,
                authViewModel = authViewModel
            )
        }

        composable(Screen.SignUp.route) {
            SignUpScreen(
                modifier = modifier,
                navController = navController,
                authViewModel = authViewModel
            )
        }

        composable(Screen.Home.route) {
            HomeScreen(
                modifier = modifier,
                navController = navController,
                authViewModel = authViewModel,
                homeViewModel = homeViewModel
            )
        }

        composable(Screen.TravelersGuide.route) {
            TravelersGuideScreen(
                navController = navController,
                travelersGuideViewModel = travelersGuideViewModel
            )
        }

        composable(Screen.ProfileSetup.route) {
            val userProfileViewModel: UserProfileViewModel = viewModel()
            ProfileSetupScreen(
                modifier = modifier,
                navController = navController,
                userProfileViewModel = userProfileViewModel
            )
        }

        composable(
            route = Screen.Content.route,
            arguments = listOf(navArgument("postId") { type = NavType.StringType })
        ) { backStackEntry ->
            val postId = backStackEntry.arguments?.getString("postId") ?: "null"
            val contentViewModel: ContentViewModel = viewModel(
                factory = ContentViewModelFactory(postId)
            )

            ContentScreen(
                modifier = modifier,
                navController = navController,
                viewModel = contentViewModel
            )
        }

        composable(Screen.UserProfile.route) {
            val userProfileViewModel: UserProfileViewModel = viewModel()
            UserProfileScreen(
                modifier = modifier,
                navController = navController,
                userProfileViewModel = userProfileViewModel
            )
        }

        composable(Screen.UploadPost.route) {
            UploadPostScreen(
                modifier = modifier,
                navController = navController,
                uploadPostViewModel = uploadPostViewModel
            )
        }

        composable(
            route = Screen.HashtagSearch.route,
            arguments = listOf(navArgument("tag") { type = NavType.StringType })
        ) { backStackEntry ->
            val tagArgument = backStackEntry.arguments?.getString("tag") ?: ""
            HashtagSearchScreen(
                navController = navController,
                viewModel = homeViewModel,
                tagArgument = tagArgument
            )
        }
    }
}
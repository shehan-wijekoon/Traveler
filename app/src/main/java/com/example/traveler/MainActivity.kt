package com.example.traveler

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import com.example.traveler.controllers.Navigation
import com.example.traveler.ui.screens.ContentScreen
import com.example.traveler.ui.screens.DiscoverScreen
import com.example.traveler.ui.screens.SignUpScreen
import com.example.traveler.ui.screens.TravelersGuideScreen
import com.example.traveler.ui.theme.TravelerTheme
import com.example.traveler.viewmodel.AuthViewModel
import com.example.traveler.viewmodel.UserProfileViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            TravelerTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    AppNavigation()
                }
            }
        }
    }
}

@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    val authViewModel: AuthViewModel = viewModel()
    val userProfileViewModel: UserProfileViewModel = viewModel()

    Navigation(
        navController = navController,
        authViewModel = authViewModel,
        userProfileViewModel = userProfileViewModel
    )
}

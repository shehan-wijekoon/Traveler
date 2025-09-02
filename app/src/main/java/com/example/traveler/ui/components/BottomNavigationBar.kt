package com.example.traveler.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.traveler.controllers.Screen

// Define a sealed class for your nav bar items
sealed class NavItem(val route: String, val icon: @Composable () -> Unit) {
    object Home : NavItem(
        route = Screen.Home.route,
        icon = { Icon(Icons.Default.Home, contentDescription = "Home") }
    )
    object Add : NavItem(
        route = "add", // You can define a new route for adding content
        icon = { Icon(Icons.Default.Add, contentDescription = "Add") }
    )
    object Profile : NavItem( // ⚠️ Changed from "More" to "Profile"
        route = Screen.ProfileSetup.route, // ⚠️ Use the correct route for the profile screen
        icon = { Icon(Icons.Default.Person, contentDescription = "Profile") }
    )
    object More : NavItem(
        route = "more", // A generic route for more options
        icon = { Icon(Icons.Default.MoreVert, contentDescription = "More") }
    )
}

@Composable
fun BottomNavigationBar(navController: NavController) {
    val navItems = listOf(NavItem.Home, NavItem.Add, NavItem.Profile)

    NavigationBar(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)),
        containerColor = Color(0xFF556B2F), // Use the dark green color from the image
        tonalElevation = 4.dp
    ) {
        navItems.forEach { item ->
            val isSelected = navController.currentDestination?.route == item.route
            NavigationBarItem(
                selected = isSelected,
                onClick = {
                    navController.navigate(item.route) {
                        popUpTo(navController.graph.startDestinationId)
                        launchSingleTop = true
                    }
                },
                icon = {
                    Box(
                        modifier = Modifier
                            .size(56.dp)
                            .clip(RoundedCornerShape(50.dp))
                            .background(if (isSelected) Color.Black else Color.White), // Black for selected, White for others
                        contentAlignment = Alignment.Center
                    ) {
                        item.icon()
                    }
                },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = Color.White,
                    unselectedIconColor = Color.Black,
                    indicatorColor = Color.Transparent // Hide the default indicator
                )
            )
        }
    }
}
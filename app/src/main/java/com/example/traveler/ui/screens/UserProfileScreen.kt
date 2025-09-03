package com.example.traveler.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.traveler.R
import com.example.traveler.ui.components.HeaderBar
import com.example.traveler.ui.components.ContentCard
import com.example.traveler.viewmodel.UserProfileViewModel
import com.example.traveler.controllers.Screen
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import com.example.traveler.viewmodel.ProfileUiState
import coil.compose.rememberAsyncImagePainter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserProfileScreen(
    modifier: Modifier = Modifier,
    navController: NavController,
    userProfileViewModel: UserProfileViewModel
) {
    // ⚠️ Correctly collect the single state flow
    val profileUiState by userProfileViewModel.profileUiState.collectAsState()

    // Mock data for user posts (kept as you requested)
    val userPosts = listOf(
        R.drawable.amazon_forest to "1",
        R.drawable.amazon_forest_2 to "2",
        R.drawable.amazon_forest_3 to "3",
        R.drawable.jungle_image to "4",
    )

    Scaffold(
        topBar = {
            HeaderBar(
                onBackClick = { navController.popBackStack() },
                onMenuClick = { /* Handle menu click */ }
            )
        }
    ) { paddingValues ->
        // ⚠️ Use the unified state
        when (val state = profileUiState) {
            is ProfileUiState.Loading -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            }
            is ProfileUiState.Success -> {
                val userProfile = state.userProfile
                Column(
                    modifier = modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                        .verticalScroll(rememberScrollState()),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Display the profile picture from the URL, if available
                    if (userProfile.profilePictureUrl != null) {
                        Image(
                            painter = rememberAsyncImagePainter(model = userProfile.profilePictureUrl),
                            contentDescription = "Profile Picture",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .size(120.dp)
                                .clip(CircleShape)
                                .shadow(4.dp, CircleShape)
                        )
                    } else {
                        Image(
                            painter = painterResource(id = R.drawable.ic_profile_placeholder),
                            contentDescription = "Profile Picture",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .size(120.dp)
                                .clip(CircleShape)
                                .shadow(4.dp, CircleShape)
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = userProfile.name,
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "@${userProfile.username}",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 32.dp),
                        horizontalArrangement = Arrangement.SpaceAround
                    ) {
                        StatColumn("Posts", "10")
                        StatColumn("Followers", "567")
                        StatColumn("Following", "124")
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    Text(
                        text = "My Posts",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(start = 16.dp, bottom = 8.dp),
                        textAlign = TextAlign.Start
                    )

                    LazyVerticalGrid(
                        columns = GridCells.Fixed(2),
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(max = 600.dp),
                        horizontalArrangement = Arrangement.spacedBy(16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        items(userPosts) { (imageResId, postId) ->
                            ContentCard(
                                imageResId = imageResId,
                                author = "@johndoe123",
                                rating = "4.5",
                                onClick = {
                                    navController.navigate(Screen.Content.createRoute(postId))
                                }
                            )
                        }
                    }
                }
            }
            is ProfileUiState.Error -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(text = state.message, color = MaterialTheme.colorScheme.error)
                }
            }
            is ProfileUiState.Idle -> {
                // Display nothing or a placeholder while waiting
            }
        }
    }
}

@Composable
fun StatColumn(label: String, value: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = value,
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}
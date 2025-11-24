package com.example.traveler.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.traveler.ui.components.HeaderBar
import com.example.traveler.ui.components.ContentCard
import com.example.traveler.viewmodel.HomeViewModel
import com.example.traveler.controllers.Screen

@Composable
fun HashtagSearchScreen(
    navController: NavController,
    viewModel: HomeViewModel,
    tagArgument: String
) {
    val cleanTag = tagArgument.removePrefix("#")

    LaunchedEffect(tagArgument) {
        viewModel.fetchPostsByHashtag(tagArgument)
    }

    // Collect the filtered results from HomeViewModel
    val posts by viewModel.hashtagPosts.collectAsState()

    Scaffold(
        topBar = {
            HeaderBar(
                onBackClick = { navController.popBackStack() },
                title = "#$cleanTag",
                onMenuClick = { /* No menu action needed for search results */ }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (posts.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(
                        "No posts found for #$cleanTag",
                        style = MaterialTheme.typography.titleMedium
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    contentPadding = PaddingValues(vertical = 16.dp)
                ) {
                    items(posts) { post ->
                        val imageUrl = post.imageUrls.firstOrNull() ?: ""
                        ContentCard(
                            imageUrl = imageUrl,
                            author = post.title,
                            onClick = {
                                navController.navigate(Screen.Content.createRoute(post.id))
                            }
                        )
                    }
                }
            }
        }
    }
}
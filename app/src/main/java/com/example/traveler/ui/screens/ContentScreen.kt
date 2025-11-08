package com.example.traveler.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.traveler.ui.components.DescriptionSection
import com.example.traveler.ui.components.HeaderBar
import com.example.traveler.ui.components.ImageCard
import com.example.traveler.viewmodel.ContentUiState
import com.example.traveler.viewmodel.ContentViewModel

@Composable
fun ContentScreen(
    modifier: Modifier = Modifier,
    navController: NavController,
    viewModel: ContentViewModel
) {
    val uiState by viewModel.uiState.collectAsState()

    Column(
        modifier = modifier.fillMaxSize()
    ) {

        HeaderBar(
            onBackClick = { navController.popBackStack() },
            onMenuClick = { /* TODO: Handle menu */ }
        )


        when (uiState) {
            is ContentUiState.Loading -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            }
            is ContentUiState.Error -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("Error loading post: ${(uiState as ContentUiState.Error).message}")
                }
            }
            is ContentUiState.Success -> {
                val post = (uiState as ContentUiState.Success).post


                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                ) {

                    val imageUrls = listOf(post.imageUrl)

                    ImageCard(
                        imageUrls = imageUrls,
                        title = post.title,
                        location = post.category
                    )

                    Spacer(modifier = Modifier.height(16.dp))
                    DescriptionSection(
                        description = post.description
                    )

                    Spacer(modifier = Modifier.height(32.dp))
                }
            }
        }
    }
}
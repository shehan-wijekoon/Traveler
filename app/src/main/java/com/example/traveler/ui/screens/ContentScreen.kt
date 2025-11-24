package com.example.traveler.ui.screens

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.traveler.ui.components.DescriptionSection
import com.example.traveler.ui.components.HeaderBar
import com.example.traveler.ui.components.ImageCard
import com.example.traveler.viewmodel.ContentUiState
import com.example.traveler.viewmodel.ContentViewModel
import com.example.traveler.controllers.Screen
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.material3.ExperimentalMaterial3Api

@OptIn(ExperimentalLayoutApi::class, ExperimentalMaterial3Api::class)
@Composable
fun ContentScreen(
    modifier: Modifier = Modifier,
    navController: NavController,
    viewModel: ContentViewModel
) {
    val context = LocalContext.current
    val uiState by viewModel.uiState.collectAsState()

    Column(
        modifier = modifier.fillMaxSize()
    ) {

        HeaderBar(
            onBackClick = { navController.popBackStack() },
            onMenuClick = { /* TODO: Handle menu */ }
        )


        when (val state = uiState) {
            is ContentUiState.Loading -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            }
            is ContentUiState.Error -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("Error loading post: ${state.message}")
                }
            }
            is ContentUiState.Success -> {
                val post = state.post

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                ) {

                    val imageUrls = post.imageUrls

                    Box(modifier = Modifier.fillMaxWidth()) {
                        ImageCard(
                            imageUrls = imageUrls,
                            title = post.title,
                            location = post.category
                        )

                        // Google Map Button
                        if (post.googleMapLink.isNotBlank()) {
                            Button(
                                onClick = {
                                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(post.googleMapLink))
                                    if (intent.resolveActivity(context.packageManager) != null) {
                                        context.startActivity(intent)
                                    }
                                },
                                modifier = Modifier
                                    .align(Alignment.TopEnd)
                                    .padding(16.dp)
                                    .size(48.dp)
                                    .clip(CircleShape),
                                contentPadding = PaddingValues(0.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.LocationOn,
                                    contentDescription = "View Location on Map",
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                        }
                    }


                    Spacer(modifier = Modifier.height(16.dp))

                    DescriptionSection(
                        description = post.description
                    )

                    if (post.hashtags.isNotEmpty()) {
                        FlowRow(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 8.dp),
                            horizontalArrangement = Arrangement.Start,
                            verticalArrangement = Arrangement.Top,

                        ) {
                            post.hashtags.forEach { tag ->
                                Text(
                                    text = "#$tag",
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = FontWeight.SemiBold,
                                    color = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier
                                        .clickable {
                                            navController.navigate(Screen.HashtagSearch.createRoute(tag))
                                        }
                                        .padding(end = 8.dp, bottom = 4.dp)
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                    }

                    if (post.rulesGuidance.isNotBlank()) {
                        Spacer(modifier = Modifier.height(24.dp))
                        Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                            Text(
                                text = "Visitor Rules & Guidance",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.primary
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = post.rulesGuidance,
                                style = MaterialTheme.typography.bodyMedium,
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(32.dp))
                }
            }
        }
    }
}
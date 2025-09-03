package com.example.traveler.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.traveler.ui.components.DescriptionSection
import com.example.traveler.ui.components.HeaderBar
import com.example.traveler.ui.components.ImageCard
import com.example.traveler.ui.components.InfoCard
import com.example.traveler.R
import androidx.navigation.NavController

@Composable
fun ContentScreen(
    modifier: Modifier = Modifier,
    navController: NavController,
    postId: String
) {
    val scrollState = rememberScrollState()
    val imageList = listOf(
        painterResource(id = R.drawable.amazon_forest),
        painterResource(id = R.drawable.amazon_forest_2),
        painterResource(id = R.drawable.amazon_forest_3)
    )

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
    ) {
        HeaderBar(
            onBackClick = { navController.popBackStack() },
            onMenuClick = { /* Handle menu */ }
        )

        ImageCard(
            imageResList = imageList,
            title = "Amazon Rain Forest",
            location = "Codajás, State of Amazonas, Brazil"
        )

        Spacer(modifier = Modifier.height(16.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            InfoCard("Distance", "450KM")
            InfoCard("Temp", "20°C")
            InfoCard("Ratings", "4.5")
        }

        Spacer(modifier = Modifier.height(16.dp))

        DescriptionSection(
            description = "The Amazon rainforest, covering much of northwestern Brazil and extending into Colombia, Peru and other South American countries, is the world’s largest tropical rainforest, famed for its biodiversity. It’s crisscrossed by thousands of rivers, including the powerful Amazon."
        )
    }
}
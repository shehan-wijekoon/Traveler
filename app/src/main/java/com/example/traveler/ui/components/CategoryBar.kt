package com.example.traveler.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.traveler.ui.theme.TravelerTheme

@Composable
fun CategoryBar(
    categories: List<String>,
    onCategorySelected: (String) -> Unit
) {
    // Keep track of the currently selected category
    var selectedCategory by remember { mutableStateOf(categories.firstOrNull() ?: "") }

    LazyRow(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(20.dp),
        contentPadding = PaddingValues(horizontal = 16.dp)
    ) {
        items(categories) { category ->
            val isSelected = category == selectedCategory
            Column(
                modifier = Modifier
                    .clickable {
                        selectedCategory = category
                        onCategorySelected(category)
                    }
            ) {
                Text(
                    text = category,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                    color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                )
                if (isSelected) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Divider(
                        modifier = Modifier.width(35.dp),
                        thickness = 2.dp,
                        color = Color.Black // Match the underline from the image
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun CategoryBarPreview() {
    TravelerTheme {
        CategoryBar(
            categories = listOf("Jungle", "Beach", "Forest", "Mountain", "Waterfall", "Desert"),
            onCategorySelected = {}
        )
    }
}
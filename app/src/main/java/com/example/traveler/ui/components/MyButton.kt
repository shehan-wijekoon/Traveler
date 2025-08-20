package com.example.traveler.ui.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.unit.dp

@Composable
fun MyButton(
    text: String,
    modifier: Modifier = Modifier.fillMaxWidth(),
    onClick: () -> Unit,
    // Add this new parameter
    isLoading: Boolean = false
) {
    Button(
        onClick = onClick,
        modifier = modifier,
        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4B5320)),
        shape = RoundedCornerShape(30.dp),
        // Disable the button when isLoading is true
        enabled = !isLoading
    ) {
        if (isLoading) {
            // Show a progress indicator when the button is in a loading state
            CircularProgressIndicator(
                modifier = Modifier.size(24.dp),
                color = Color.White
            )
        } else {
            // Show the text normally
            Text(text = text, color = Color.White)
        }
    }
}
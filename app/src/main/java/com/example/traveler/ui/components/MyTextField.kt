package com.example.traveler.ui.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@Composable
fun MyTextField(
    state: TextFieldState,
    placeholder: String
) {
    OutlinedTextField(
        value = state.text,
        onValueChange = { state.text = it },
        label = { Text(placeholder ,
                        style = MaterialTheme.typography.bodyMedium.copy(
                        fontWeight = FontWeight.Normal,
                        color = Color.DarkGray) )},
        modifier = Modifier
            .fillMaxWidth()
            .height(60.dp),
        shape = RoundedCornerShape(15.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = Color.Black,
            unfocusedBorderColor = Color.Gray,
            focusedLabelColor = Color.DarkGray,
            unfocusedLabelColor = Color.Gray,
            focusedContainerColor = Color(0xFFEAEAEA),
            unfocusedContainerColor = Color(0xFFEAEAEA),
            cursorColor = Color.Black
        )
    )
}


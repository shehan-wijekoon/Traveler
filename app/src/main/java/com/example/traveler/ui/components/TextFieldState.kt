package com.example.traveler.ui.components

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.Stable

class TextFieldState(initialText: String = "") {
    var text: String by mutableStateOf(initialText)
}

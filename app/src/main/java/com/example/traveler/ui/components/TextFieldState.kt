package com.example.traveler.ui.components

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue

class TextFieldState(initialText: String = "") {
    var text by mutableStateOf(initialText)
}

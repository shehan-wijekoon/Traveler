package com.example.traveler

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.example.traveler.ui.theme.TravelerTheme
import com.example.traveler.ui.screens.LoginScreen

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            TravelerTheme {
                LoginScreen()
            }
        }
    }
}
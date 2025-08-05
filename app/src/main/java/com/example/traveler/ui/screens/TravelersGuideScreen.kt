package com.example.traveler.ui.screens

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.traveler.viewmodel.TravelersGuideViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TravelersGuideScreen(
    travelersGuideViewModel: TravelersGuideViewModel = viewModel()
) {
    val temperature by travelersGuideViewModel.temperature.collectAsState()
    val humidity by travelersGuideViewModel.humidity.collectAsState()
    val pressure by travelersGuideViewModel.pressure.collectAsState()
    val altitude by travelersGuideViewModel.altitude.collectAsState()

    // FIX: Changed to 'historicalReadings' to match the ViewModel
    val historicalReadings by travelersGuideViewModel.historicalReadings.collectAsState()

    val predictedData by travelersGuideViewModel.predictedData.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Travelers guide") },
                navigationIcon = {
                    IconButton(onClick = { /* Handle back */ }) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = { /* Handle menu */ }) {
                        Icon(Icons.Filled.MoreVert, contentDescription = "Menu")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                DataCircle(label = "Temperature", value = temperature)
                DataCircle(label = "Humidity", value = humidity)
                DataCircle(label = "Pressure", value = pressure)
            }

            Spacer(modifier = Modifier.height(16.dp))

            Card(modifier = Modifier.fillMaxWidth()) {
                Box(modifier = Modifier.padding(24.dp), contentAlignment = Alignment.Center) {
                    Text("Altitude: $altitude")
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text("Graph", style = MaterialTheme.typography.titleMedium)
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
            ) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    // FIX: historicalReadings is a list, so we'll display a placeholder
                    // until you integrate a proper charting library.
                    Text(if (historicalReadings.isEmpty()) "No Historical Data" else "Data Available: ${historicalReadings.size} points")
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text("Predicted Data", style = MaterialTheme.typography.titleMedium)
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(150.dp)
            ) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(predictedData)
                }
            }
        }
    }
}

@Composable
fun DataCircle(label: String, value: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Box(
            modifier = Modifier
                .size(72.dp)
                .border(2.dp, Color.Gray, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Text(value)
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text(label)
    }
}
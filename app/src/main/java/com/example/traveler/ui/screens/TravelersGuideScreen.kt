package com.example.traveler.ui.screens

import androidx.compose.foundation.background
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.traveler.viewmodel.TravelersGuideViewModel
import androidx.compose.ui.viewinterop.AndroidView
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.github.mikephil.charting.data.Entry

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TravelersGuideScreen(
    travelersGuideViewModel: TravelersGuideViewModel = viewModel()
) {
    val context = LocalContext.current // added to get the context for ViewModel
    LaunchedEffect(key1 = Unit) { // added to trigger data fetching once
        // FIX: The initialize method is not in your ViewModel. We'll add a call to fetchHistoricalData() here.
        travelersGuideViewModel.initiateDataFetch()
    }

    val temperature by travelersGuideViewModel.temperature.collectAsState()
    val humidity by travelersGuideViewModel.humidity.collectAsState()
    val pressure by travelersGuideViewModel.pressure.collectAsState()
    val altitude by travelersGuideViewModel.altitude.collectAsState()

    // FIX: Changed to 'historicalReadings' to match the ViewModel
    val historicalReadings by travelersGuideViewModel.historicalReadings.collectAsState()

    val predictedData by travelersGuideViewModel.predictedData.collectAsState()

    val temperatureChartEntries by travelersGuideViewModel.temperatureChartEntries.collectAsState()
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

            // FIX: This section is completely changed to render the chart
            Text("Temperature Graph", style = MaterialTheme.typography.titleMedium)
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
            ) {
                if (temperatureChartEntries.isNotEmpty()) {
                    TemperatureLineChart(entries = temperatureChartEntries)
                } else {
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text("No Historical Data")
                    }
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
fun TemperatureLineChart(entries: List<Entry>) {
    AndroidView(
        modifier = Modifier.fillMaxSize(),
        factory = { context ->
            LineChart(context).apply {
                // Chart configuration
                description.text = "Temperature Over Time"
                description.textColor = Color.DarkGray.toArgb()
                description.textSize = 10f
                setDrawGridBackground(false)
                setTouchEnabled(true)
                isDragEnabled = true
                setScaleEnabled(true)
                setPinchZoom(true)

                // X-Axis configuration
                xAxis.apply {
                    position = XAxis.XAxisPosition.BOTTOM
                    setDrawGridLines(false)
                    setDrawAxisLine(true)
                    granularity = 1f
                    textColor = Color.DarkGray.toArgb()
                }

                // Y-Axis configuration
                axisRight.isEnabled = false // Disable right-side Y-axis
                axisLeft.apply {
                    setDrawGridLines(true)
                    setDrawAxisLine(true)
                    textColor = Color.DarkGray.toArgb()
                }

                legend.isEnabled = false
            }
        },
        update = { chart ->
            if (entries.isNotEmpty()) {
                val dataSet = LineDataSet(entries, "Temperature").apply {
                    setDrawCircles(false)
                    color = Color(0xFF2196F3).toArgb() // Use nicer blue
                    lineWidth = 2f
                    valueTextSize = 0f // Don't show data point values on the line
                }
                chart.data = LineData(dataSet)
                chart.invalidate() // Refresh the chart
            }
        }
    )
}

@Composable
fun DataCircle(label: String, value: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Box(
            modifier = Modifier
                .size(72.dp)
                .border(2.dp, Color(0xFFB0BEC5), CircleShape)
                .background(Color(0xFFF8FAFB), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Text(value, style = MaterialTheme.typography.bodyLarge)
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text(label, style = MaterialTheme.typography.bodySmall)
    }
}

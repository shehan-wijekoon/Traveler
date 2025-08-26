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
import androidx.core.content.ContextCompat
import com.example.traveler.R
import androidx.compose.ui.unit.sp
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.components.MarkerView
import com.github.mikephil.charting.highlight.Highlight
import com.github.mikephil.charting.utils.MPPointF
import android.widget.TextView
import android.view.View

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TravelersGuideScreen(
    travelersGuideViewModel: TravelersGuideViewModel = viewModel()
) {
    val context = LocalContext.current

    // THE ONLY CHANGE: Pass the 'context' to the ViewModel's data fetch function.
    // This allows the ViewModel to access the assets folder and load the AI model.
    LaunchedEffect(key1 = Unit) {
        travelersGuideViewModel.initiateDataFetch(context)
    }

    val temperature by travelersGuideViewModel.temperature.collectAsState()
    val humidity by travelersGuideViewModel.humidity.collectAsState()
    val pressure by travelersGuideViewModel.pressure.collectAsState()
    val altitude by travelersGuideViewModel.altitude.collectAsState()
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
        },
        containerColor = Color(0xFFF0F5F9)
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(horizontal = 16.dp, vertical = 8.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            TemperatureDisplay(value = temperature, modifier = Modifier.padding(vertical = 16.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                DataCard(label = "Humidity", value = humidity)
                DataCard(label = "Pressure", value = pressure)
                DataCard(label = "Altitude", value = altitude)
            }
            Spacer(modifier = Modifier.height(24.dp))
            Text("Temperature Graph", style = MaterialTheme.typography.titleLarge)
            Spacer(modifier = Modifier.height(8.dp))
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(300.dp),
                shape = MaterialTheme.shapes.medium,
                colors = CardDefaults.cardColors(containerColor = Color.White)
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
            Text("Predicted Data", style = MaterialTheme.typography.titleLarge)
            Spacer(modifier = Modifier.height(8.dp))
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(150.dp),
                shape = MaterialTheme.shapes.medium,
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(predictedData)
                }
            }
        }
    }
}
@Composable
fun TemperatureDisplay(value: String, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier
            .width(200.dp)
            .height(200.dp),
        shape = CircleShape,
        colors = CardDefaults.cardColors(containerColor = Color(0xFFE3F2FD))
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(value, style = MaterialTheme.typography.displayMedium.copy(color = Color(0xFF1976D2)))
            Text(
                "°C",
                style = MaterialTheme.typography.titleLarge.copy(
                    color = Color(0xFF1976D2),
                    fontSize = 40.sp
                )
            )
            Text("Current", style = MaterialTheme.typography.bodySmall.copy(color = Color.DarkGray))
        }
    }
}
@Composable
fun DataCard(label: String, value: String) {
    Card(
        modifier = Modifier
            .width(110.dp)
            .height(90.dp),
        shape = MaterialTheme.shapes.small,
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(label, style = MaterialTheme.typography.bodySmall, color = Color.Gray, maxLines = 1)
            Spacer(modifier = Modifier.height(4.dp))
            if (label == "Pressure" || label == "Altitude") {
                Row(
                    verticalAlignment = Alignment.Bottom,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Text(value, style = MaterialTheme.typography.bodyMedium, color = Color.Black, maxLines = 1)
                    if (label == "Pressure") {
                        Text("hPa", style = MaterialTheme.typography.bodySmall, color = Color.Gray)
                    } else if (label == "Altitude") {
                        Text("m", style = MaterialTheme.typography.bodySmall, color = Color.Gray)
                    }
                }
            } else {
                Text(value, style = MaterialTheme.typography.bodyMedium, color = Color.Black, maxLines = 1)
            }
        }
    }
}

@Composable
fun TemperatureLineChart(entries: List<Entry>) {
    val context = LocalContext.current
    AndroidView(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        factory = {
            LineChart(it).apply {
                description.isEnabled = false
                setDrawGridBackground(false)
                setTouchEnabled(true)
                isDragEnabled = true
                setScaleEnabled(true)
                setPinchZoom(true)
                xAxis.apply {
                    position = XAxis.XAxisPosition.BOTTOM
                    setDrawGridLines(false)
                    setDrawAxisLine(true)
                    textColor = Color.DarkGray.toArgb()
                    granularity = 1f
                }
                axisRight.isEnabled = false
                axisLeft.apply {
                    setDrawGridLines(true)
                    gridColor = Color.LightGray.toArgb()
                    setDrawAxisLine(false)
                    textColor = Color.DarkGray.toArgb()
                    axisMinimum = 15f
                    axisMaximum = 40f
                }
                legend.isEnabled = false
            }
        },
        update = { chart ->
            if (entries.isNotEmpty()) {
                val dataSet = LineDataSet(entries, "Temperature").apply {
                    setDrawCircles(false)
                    color = Color(0xFF2196F3).toArgb()
                    lineWidth = 3f
                    valueTextSize = 0f
                    mode = LineDataSet.Mode.CUBIC_BEZIER
                    setDrawFilled(true)
                    fillDrawable = ContextCompat.getDrawable(context, R.drawable.fade_blue)
                }
                chart.data = LineData(dataSet)
                chart.invalidate()
            }
        }
    )
}
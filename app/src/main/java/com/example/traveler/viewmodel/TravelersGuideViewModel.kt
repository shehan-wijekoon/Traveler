package com.example.traveler.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.database
import com.google.firebase.Firebase
import com.github.mikephil.charting.data.Entry
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

// Data class to represent a single data point from the database
data class Reading(
    val altitude: String,
    val pressure: String,
    val temperature: String,
    val timestamp: String
)

class TravelersGuideViewModel : ViewModel() {

    // Get an instance of the Firebase Realtime Database
    private val database = Firebase.database
    private val dbRef = database.getReference("BMP280_Readings")

    private val _temperature = MutableStateFlow("Loading...")
    val temperature: StateFlow<String> = _temperature

    // The image doesn't show humidity data. I'll keep the StateFlow but it will always be "-".
    private val _humidity = MutableStateFlow("-")
    val humidity: StateFlow<String> = _humidity

    private val _pressure = MutableStateFlow("Loading...")
    val pressure: StateFlow<String> = _pressure

    private val _altitude = MutableStateFlow("Loading...")
    val altitude: StateFlow<String> = _altitude

    // StateFlow to hold historical data for the graph
    private val _historicalReadings = MutableStateFlow<List<Reading>>(emptyList())
    val historicalReadings: StateFlow<List<Reading>> = _historicalReadings

    //StateFlow to hold the chart data in MPAndroidChart's format
    val temperatureChartEntries: StateFlow<List<Entry>> = _historicalReadings.map { readings ->
        readings.mapIndexed { index, reading ->
            Entry(index.toFloat(), reading.temperature.toFloatOrNull() ?: 0f)
        }
    }.stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    private val _predictedData = MutableStateFlow("Loading...")
    val predictedData: StateFlow<String> = _predictedData

    init {
        initiateDataFetch()
    }

    fun initiateDataFetch() {
        fetchLatestData()
        fetchHistoricalData()
    }

    private fun fetchLatestData() {
        dbRef.limitToLast(1).addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    // Iterate through the single child of the snapshot
                    for (childSnapshot in snapshot.children) {
                        val data = childSnapshot.getValue() as? Map<String, Any>
                        if (data != null) {
                            // NEW: Format the temperature to two decimal places
                            val temp = data["temperature_C"]?.toString()?.toFloatOrNull()
                            _temperature.value = if (temp != null) String.format("%.2f", temp) else "-"

                            // NEW: Format the pressure to two decimal places
                            val pressureValue = data["pressure_hPa"]?.toString()?.toFloatOrNull()
                            _pressure.value = if (pressureValue != null) String.format("%.2f", pressureValue) else "-"

                            // NEW: Format the altitude to two decimal places
                            val altitudeValue = data["altitude_meters"]?.toString()?.toFloatOrNull()
                            _altitude.value = if (altitudeValue != null) String.format("%.2f", altitudeValue) else "-"

                            _predictedData.value = "Not Implemented"
                        }
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("RealtimeDB", "Error fetching latest data", error.toException())
                _temperature.value = "Error"
                _pressure.value = "Error"
                _altitude.value = "Error"
            }
        })
    }

    private fun fetchHistoricalData() {
        // Fetch the last 100 entries for the graph
        dbRef.limitToLast(20).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val readings = mutableListOf<Reading>()
                for (childSnapshot in snapshot.children) {
                    val data = childSnapshot.getValue() as? Map<String, Any>
                    if (data != null) {
                        readings.add(
                            Reading(
                                altitude = data["altitude_meters"]?.toString() ?: "-",
                                pressure = data["pressure_hPa"]?.toString() ?: "-",
                                temperature = data["temperature_C"]?.toString() ?: "-",
                                timestamp = data["timestamp"]?.toString() ?: "-"
                            )
                        )
                    }
                }
                _historicalReadings.value = readings
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("RealtimeDB", "Error fetching historical data", error.toException())
            }
        })
    }
}
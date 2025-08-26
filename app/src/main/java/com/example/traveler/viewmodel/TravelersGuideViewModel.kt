package com.example.traveler.viewmodel

import android.content.Context
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
import org.tensorflow.lite.Interpreter
import org.tensorflow.lite.support.tensorbuffer.TensorBuffer
import org.tensorflow.lite.DataType
import java.nio.ByteBuffer
import java.nio.ByteOrder

// The Reading data class is already correct.
data class Reading(
    val altitude_meters: Double? = null,
    val pressure_hPa: Double? = null,
    val temperature_C: Double? = null,
    val timestamp: String? = null,
    val humidity_percent: Double? = null
)

class TravelersGuideViewModel : ViewModel() {

    private val database = Firebase.database
    private val dbRef = database.getReference("BMP280_Readings")

    private val _temperature = MutableStateFlow("Loading...")
    val temperature: StateFlow<String> = _temperature

    private val _humidity = MutableStateFlow("Loading")
    val humidity: StateFlow<String> = _humidity

    private val _pressure = MutableStateFlow("Loading...")
    val pressure: StateFlow<String> = _pressure

    private val _altitude = MutableStateFlow("Loading...")
    val altitude: StateFlow<String> = _altitude

    private val _historicalReadings = MutableStateFlow<List<Reading>>(emptyList())
    val historicalReadings: StateFlow<List<Reading>> = _historicalReadings

    val temperatureChartEntries: StateFlow<List<Entry>> = _historicalReadings.map { readings ->
        readings.mapIndexed { index, reading ->
            Entry(index.toFloat(), reading.temperature_C?.toFloat() ?: 0f)
        }
    }.stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    private val _predictedData = MutableStateFlow("Loading...")
    val predictedData: StateFlow<String> = _predictedData

    private var tfliteInterpreter: Interpreter? = null
    private val TEMP_MIN = 20.0f
    private val TEMP_MAX = 30.0f
    private val HUMIDITY_MIN = 70.0f
    private val HUMIDITY_MAX = 100.0f
    private val PRESSURE_MIN = 900.0f
    private val PRESSURE_MAX = 1100.0f

    init {
        // We will call initiateDataFetch from the UI.
    }

    fun loadModel(context: Context) {
        try {
            // **FIX 1:** Changed model file name to "rain_classifier.tflite" to match the assets folder.
            val modelFileDescriptor = context.assets.openFd("rain_classifier.tflite")
            val inputStream = modelFileDescriptor.createInputStream()
            val fileChannel = inputStream.channel
            val modelBuffer = fileChannel.map(
                java.nio.channels.FileChannel.MapMode.READ_ONLY,
                modelFileDescriptor.startOffset,
                modelFileDescriptor.declaredLength
            )
            tfliteInterpreter = Interpreter(modelBuffer)
            Log.d("TFLite", "Model loaded successfully with core Interpreter!")
        } catch (e: Exception) {
            Log.e("TFLite", "Error loading model: ${e.message}")
            _predictedData.value = "Model Error: ${e.message}"
        }
    }

    fun initiateDataFetch(context: Context) {
        loadModel(context)
        fetchLatestData()
        fetchHistoricalData()
    }

    private fun fetchLatestData() {
        dbRef.limitToLast(1).addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    for (childSnapshot in snapshot.children) {
                        val reading = childSnapshot.getValue(Reading::class.java)
                        if (reading != null) {
                            _temperature.value = reading.temperature_C?.let { String.format("%.2f", it) } ?: "-"
                            _humidity.value = reading.humidity_percent?.let { String.format("%.2f", it) } ?: "-"
                            _pressure.value = reading.pressure_hPa?.let { String.format("%.2f", it) } ?: "-"
                            _altitude.value = reading.altitude_meters?.let { String.format("%.2f", it) } ?: "-"
                        }
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("RealtimeDB", "Error fetching latest data", error.toException())
                _temperature.value = "Error"
                _pressure.value = "Error"
                _altitude.value = "Error"
                _humidity.value = "Error"
            }
        })
    }

    private fun fetchHistoricalData() {
        dbRef.limitToLast(30).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val readings = mutableListOf<Reading>()
                for (childSnapshot in snapshot.children) {
                    val reading = childSnapshot.getValue(Reading::class.java)
                    if (reading != null) {
                        readings.add(reading)
                    }
                }
                _historicalReadings.value = readings
                runPrediction()
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("RealtimeDB", "Error fetching historical data", error.toException())
            }
        })
    }

    private fun runPrediction() {
        val interpreter = tfliteInterpreter ?: return
        val historicalData = _historicalReadings.value

        // **FIX 2:** Changed prediction logic to use the last single data point.
        if (historicalData.isEmpty()) {
            _predictedData.value = "No historical data available for prediction"
            return
        }

        val latestReading = historicalData.last()

        // **FIX 3:** Corrected the input tensor size to [1, 3] to match a single data point with 3 features.
        val inputBuffer = TensorBuffer.createFixedSize(
            intArrayOf(1, 3),
            DataType.FLOAT32
        )
        val inputByteBuffer = inputBuffer.buffer.order(ByteOrder.nativeOrder())

        // Get the values and handle nulls
        val temp = latestReading.temperature_C?.toFloat() ?: 0f
        val humidity = latestReading.humidity_percent?.toFloat() ?: 0f
        val pressure = latestReading.pressure_hPa?.toFloat() ?: 0f

        // Apply scaling and put into the buffer
        inputByteBuffer.putFloat(normalize(temp, TEMP_MIN, TEMP_MAX))
        inputByteBuffer.putFloat(normalize(humidity, HUMIDITY_MIN, HUMIDITY_MAX))
        inputByteBuffer.putFloat(normalize(pressure, PRESSURE_MIN, PRESSURE_MAX))

        // **FIX 4:** Corrected the output buffers to match a single-output model.
        // The previous code was set up for a multi-output model which is not compatible.
        val outputBuffer = TensorBuffer.createFixedSize(
            intArrayOf(1, 3), // The output for rain classification is typically [1, num_classes]
            DataType.FLOAT32
        )
        val outputs = mapOf(0 to outputBuffer.buffer)

        try {
            // runForMultipleInputsOutputs is for multi-input/output. Using run for single input/output.
            interpreter.run(
                inputBuffer.buffer,
                outputBuffer.buffer
            )

            val rainProbabilities = outputBuffer.floatArray
            val rainPrediction = getRainPrediction(rainProbabilities)

            // Since the model is a "rain classifier" it only predicts rain.
            // The previous code for predicting temp/humidity/pressure is for a different model.
            _predictedData.value = "Next:\nRain: $rainPrediction"

        } catch (e: Exception) {
            _predictedData.value = "Prediction Error: ${e.message}"
            Log.e("TFLite", "Prediction failed: ${e.message}")
        }
    }

    private fun normalize(value: Float, min: Float, max: Float): Float {
        if (max - min == 0f) return 0f
        return (value - min) / (max - min)
    }

    private fun getRainPrediction(output: FloatArray): String {
        val labels = listOf("No Rain", "Light Rain", "Heavy Rain")
        val maxIndex = output.indices.maxByOrNull { output[it] } ?: 0
        return labels[maxIndex]
    }

    override fun onCleared() {
        super.onCleared()
        tfliteInterpreter?.close()
        tfliteInterpreter = null
    }
}
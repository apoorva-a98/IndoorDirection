package com.example.magnetometer

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorManager
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.magnetometer.ui.theme.MagnetometerTheme

private val TAG = "MAIN"
private lateinit var  sensorManager: SensorManager
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Flow for Identifiying Sensors
        // https://developer.android.com/guide/topics/sensors/sensors_overview#sensors-identify
        // Initialize the SensorManager
        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager

        //List all of the Sensors on this device
        // returns a List that contains the type 'Sensor'
        val deviceSensors: List<Sensor> = sensorManager.getSensorList(Sensor.TYPE_ALL)
        // Use the List's Iterator method to write all the sensors to the log
        // .forEach method uses 'it' to represent the value at the iterator's index
        // https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/for-each.html#foreach

        deviceSensors.forEach {
            // the '$' inside the quotes allows one to insert a variable into the string
            // String Templates - https://kotlinlang.org/docs/strings.html#string-templates
            Log.d(TAG, "Device Sensor:$it ")
        }

        setContent {
            MagnetometerTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {
                    Greeting("Android")
                }
            }
        }
    }
}

@Composable
fun Greeting(name: String) {
    Text(text = "Hello $name!")
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    MagnetometerTheme {
        Greeting("Android")
    }
}
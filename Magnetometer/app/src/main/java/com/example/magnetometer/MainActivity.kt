package com.example.magnetometer

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
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

private const val TAG = "MAIN"
private lateinit var  sensorManager: SensorManager
private lateinit var gameOrientationVector: Sensor

/* To the right of the ':' below is the class that MainActivity inherits. After the comma we have SensorEventListener which is an Interface*/
class MainActivity : ComponentActivity(), SensorEventListener {
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

        // Todo cleaned this up by placing it in it's own function
        listSensors(sensorManager = sensorManager)
        gameOrientationVector = sensorManager.getDefaultSensor(Sensor.TYPE_GAME_ROTATION_VECTOR)


        //TODO This registration activates an Observer that is listening for state changes in the sensor data
        sensorManager.registerListener(this, gameOrientationVector, SensorManager.SENSOR_DELAY_NORMAL)

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

        override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
            Log.d(TAG, "onAccuracyChanged: DO Something")
        }
        /* This observer function is listening for changes in value, once you register the listener above, it will
        continually update
        */
        override fun onSensorChanged(event: SensorEvent?) {
            if (event != null) {
                /* TODO These if statements will make the device LESS sensitive to small movements...
                I am doing this to illustrate how we can use the phone orientation as a trigger vs. a controller
                Play with these values. The response range should be tuned to the interaction you are trying to author
                 */
                // the 'f' after the number value means the number is a Float - https://kotlinlang.org/docs/numbers.html#literal-constants-for-numbers

                if(event.values[0] > 0.25f ){
                    Log.d(TAG, "X is positive")
                } else if( event.values[0] < -0.25f ){
                    Log.d(TAG, "X is negative")
                }
                if (event.values[1] > 0.25f) {
                    Log.d(TAG, "Y is positive")
                } else if (event.values[1] < -0.25f) {
                    Log.d(TAG, "Y is negative")
                }
                if (event.values[2] > 0.55f) {
                    Log.d(TAG, "Z is positive")
                    Log.d(TAG, "\n Z: ${event.values[2]}")
                } else if (event.values[2] < -0.80f) {
                    Log.d(TAG, "\n Z: ${event.values[2]}")
                    Log.d(TAG, "Z is negative")
                }
            }

        }

        /* Applications have lifecycles. When we launch the app, it is in the Foreground, if we switch to another app, it moves to the background.
        Activity Lifecycle - https://developer.android.com/guide/components/activities/activity-lifecycle
        When the app is in the background it is paused. onPause, therefore we stop listening to the sensor.
         */
        override fun onPause() {
            super.onPause()
            sensorManager.unregisterListener(this)
        }
        /* When we bring the already running app back to the foreground
         we are resuming the app. onResume(), executes the code that restarts the listener by calling the .registerListener method
         */
        override fun onResume() {
            super.onResume()
            gameOrientationVector?.also { gameOrientatVec ->
                sensorManager.registerListener(this,gameOrientatVec, SensorManager.SENSOR_DELAY_NORMAL)
            }

        }
    }

    override fun onSensorChanged(p0: SensorEvent?) {
        TODO("Not yet implemented")
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
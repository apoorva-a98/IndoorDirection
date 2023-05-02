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
import androidx.compose.foundation.layout.*
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.magnetometer.ui.theme.MagnetometerTheme
import java.lang.Math.atan
import java.lang.Math.cos
import java.lang.Math.sin
import java.lang.Math.sqrt

private const val TAG = "MAIN"
private lateinit var  sensorManager: SensorManager
//define a variable that you will initialize later in the class
// lateinit https://kotlinlang.org/docs/properties.html#late-initialized-properties-and-variables
private lateinit var directionVector: Sensor
/* To the right of the ':' below is the class that MainActivity inherits. After the comma we have SensorEventListener which is an Interface
Inheritance - https://kotlinlang.org/docs/inheritance.html
Interface -  https://kotlinlang.org/docs/interfaces.html
 */
private var compassAngle: Float = 0f
private var magneticDeclination: Float = 0f

class MainActivity : ComponentActivity(), SensorEventListener {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        listSensors(sensorManager = sensorManager)

        directionVector = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD)
        /*In Android, the TYPE_MAGNETIC_FIELD sensor is typically used to measure the Earth's magnetic field, which can be used to determine the device's orientation and direction.
        The TYPE_ORIENTATION sensor was deprecated in API level 3 and is not recommended for use. The TYPE_GEOMAGNETIC_ROTATION_VECTOR and TYPE_MAGNETIC_FIELD_UNCALIBRATED sensors are alternative options that provide more advanced features, such as higher accuracy or uncalibrated readings, but are not necessary for basic compass functionality.
        Therefore, the recommended sensor to use for a compass application in Android is the TYPE_MAGNETIC_FIELD sensor.*/

        // Obtain magnetic declination for current location (in degrees)
        // This value can be obtained from a magnetic declination map or a third-party API
        magneticDeclination = -12.37f // In Brooklyn Heights NYC https://www.magnetic-declination.com/#

        //TODO This registration activates an Observer that is listening for state changes in the sensor data
        sensorManager.registerListener(this, directionVector, SensorManager.SENSOR_DELAY_NORMAL)
        /*This delay indicates the frequency with which the sensor readings are updated and provided to the app. In particular, SENSOR_DELAY_NORMAL specifies a delay of around 200,000 microseconds or 200 milliseconds, which means that the sensor readings will be delivered at a rate of approximately 5 times per second.*/

        setContent {
            val modifier: Modifier = Modifier
            // TODO the theme for the app is located @ ui.theme > Theme.kt and is generated automatically
            MagnetometerTheme {
                // A surface container using the 'background' color from the theme
                // TODO Hover over the methods after 'Modifier' to see what they configure in the view
                Surface(
                    modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    MainApplicationScreen(modifier,sensorArrayState)
                }
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
            /* TODO These if statements will make the device LESS sensitive to small movements...*/
            sensorArrayState.value = event.values
            /*I am doing this to illustrate how we can use the phone orientation as a trigger vs. a controller
            Play with these values. The response range should be tuned to the interaction you are trying to author
             */
            // the 'f' after the number value means the number is a Float - https://kotlinlang.org/docs/numbers.html#literal-constants-for-numbers
            // Log.d(TAG, (event.values[0] + event.values[1] + event.values[2]).toString())
            val dx = event.values[0];
            val dy = event.values[1];
            val dz = event.values[2];

            // Calculate total magnetic field strength (in microtesla)
//            val magnetic_field : Double = sqrt((dx * dx + dy * dy + dz * dz).toDouble())

            // Calculate inclination angle (in radians)
            val inclination_angle = atan(dy / sqrt((dx * dx + dz * dz).toDouble()))

            // Calculate azimuth angle (in degrees)
            val compassAngle = ((360 + atan(dx / (dy * sin(inclination_angle) + dz * cos(inclination_angle)))) % 360 - magneticDeclination)
            Log.d(TAG, compassAngle.toString())
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
        directionVector.also { directionVector ->
            sensorManager.registerListener(this, directionVector, SensorManager.SENSOR_DELAY_NORMAL)
        }
    }

}

@Composable
fun SensorValueDisplay(modifier: Modifier,
                       sensorValues : MutableState<FloatArray>){
    Column (
        modifier.padding(8.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ){
        Text(text = "X: ${sensorValues.value[0]}")
        Text(text = "Y: ${sensorValues.value[1]}")
        Text(text = "Z: ${sensorValues.value[2]}")
    }
}
@Composable
fun MainApplicationScreen(
    modifier: Modifier,
    sensorValues: MutableState<FloatArray>
){
//    val sensorValueOutput by remember { mutableStateOf(0) }
    Column(
        modifier = Modifier.padding(32.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ){
        Text(
            fontSize = 24.sp,
            modifier = Modifier.align(Alignment.CenterHorizontally),
            text = "Hello Firebase Sensors")
        Spacer(Modifier.height(16.dp))
        SensorValueDisplay(modifier,
            sensorValues)
    }
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    MagnetometerTheme {
        MainApplicationScreen()
    }
}
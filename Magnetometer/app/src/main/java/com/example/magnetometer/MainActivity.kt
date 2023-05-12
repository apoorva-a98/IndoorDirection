package com.example.magnetometer

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.net.ConnectivityManager
import android.net.wifi.WifiManager
import android.os.Build
import android.os.Bundle
import android.os.VibrationEffect
import android.os.Vibrator
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.app.ActivityCompat
import com.example.magnetometer.ui.theme.MagnetometerTheme
import kotlin.math.roundToInt

val TAG1 : String = "WIFI"
@SuppressLint("StaticFieldLeak")
lateinit var ctx: Context

private const val TAG = "MAIN"
private lateinit var  sensorManager: SensorManager
//define a variable that you will initialize later in the class
// lateinit https://kotlinlang.org/docs/properties.html#late-initialized-properties-and-variables
private lateinit var directionVector: Sensor
/* To the right of the ':' below is the class that MainActivity inherits. After the comma we have SensorEventListener which is an Interface
Inheritance - https://kotlinlang.org/docs/inheritance.html
Interface -  https://kotlinlang.org/docs/interfaces.html
 */

class MainActivity : ComponentActivity(), SensorEventListener {
    override fun onCreate(savedInstanceState: Bundle?) {
        //Beginning Permissions Check
        ctx = applicationContext

        // [start] Permissions Check
        permissionsResultLauncher =
            registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { result ->
                if (result[android.Manifest.permission.ACCESS_FINE_LOCATION] != null) {
                    isLocFinePermissionGranted =
                        true == result[Manifest.permission.ACCESS_FINE_LOCATION]
                }
                if (result[android.Manifest.permission.ACCESS_NETWORK_STATE] != null) {
                    isNetworkPermissionGranted =
                        true == result[Manifest.permission.ACCESS_NETWORK_STATE]
                }
                if (result[android.Manifest.permission.ACCESS_WIFI_STATE] != null) {
                    isWifiPermissionGranted =
                        true == result[Manifest.permission.ACCESS_WIFI_STATE]
                }
            }

        if (ActivityCompat.checkSelfPermission(
                ctx,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            // If all permissions are granted, Proceed
            getWifiInfo(ctx)
        } else {
            requestPermission(applicationContext)
        }
// [end] Permissions Check


        super.onCreate(savedInstanceState)
        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        listSensors(sensorManager = sensorManager)

        directionVector = sensorManager.getDefaultSensor(Sensor.TYPE_GEOMAGNETIC_ROTATION_VECTOR)


        //TODO This registration activates an Observer that is listening for state changes in the sensor data
        sensorManager.registerListener(this, directionVector, SensorManager.SENSOR_DELAY_NORMAL)
        /*This delay indicates the frequency with which the sensor readings are updated and provided to the app. In particular, SENSOR_DELAY_NORMAL specifies a delay of around 200,000 microseconds or 200 milliseconds, which means that the sensor readings will be delivered at a rate of approximately 5 times per second.*/

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
            getWifiInfo(this)
        }
    }

    private fun getWifiInfo(context: Context) {
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val wifiManager =
            context.applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager
        if (ActivityCompat.checkSelfPermission(
                ctx,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            // If all permissions are granted, Proceed
//        wifiManager.scanResults
//            Log.d(TAG, "getWifiInfo: ${wifiManager.scanResults}")
            val wifiInfo = wifiManager.connectionInfo
            val bssid = wifiInfo.bssid
            Log.d(TAG, "getWifiInfo: BSSID=$bssid")
        } else {
            requestPermission(ctx)
        }

        //WifiInfo is depricated and doesnt work
        //connectionInfo requires Wifi info and hence doesnt work
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
        Log.d(TAG, "onAccuracyChanged: DO Something")
    }

    /* This observer function is listening for changes in value, once you register the listener above, it will
    continually update
    */
    override fun onSensorChanged(event: SensorEvent?) {
        if (event != null) {
            val dz = event.values[2]
            var ztoDegrees: Double = Math.toDegrees(dz.toDouble())
            ztoDegrees = (ztoDegrees).roundToInt().toDouble()

            if (ztoDegrees in 13.0..37.0) {
                val vibrator = getSystemService(Vibrator::class.java)
                vibrator.vibrate(
                    VibrationEffect.createOneShot(
                        500,
                        VibrationEffect.DEFAULT_AMPLITUDE
                    )
                )
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
        directionVector.also { directionVector ->
            sensorManager.registerListener(this, directionVector, SensorManager.SENSOR_DELAY_NORMAL)
        }
    }

    
    //TODO Add a button
    //TODO onclick speak what area the user is in
    //TODO say now turn to find the direction of..>> just say
    //TODO when pointing in the direction give a haptic response(buz only if the threshold is more than 20/30) to make the user stop and say what is in the direction.
    //TODO click to stop

    //last angle something
    //if the router connected gto is ###
    //if (the magnometer range is between .. and .. ){
    //if ( last angle was something else){
    //vibrate()
    //speak()
    //}
    //else{
    //do nothing
    //}
    //}
    //if (the magnometer range is between .. and ...&& last change  = 0){
    //vibrate()
    //speak()
    //}


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
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
import android.speech.tts.TextToSpeech
import android.speech.tts.TextToSpeech.QUEUE_FLUSH
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat
import com.example.magnetometer.ui.theme.MagnetometerTheme
import java.util.Locale
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
var results : String = ""
var wifiInfo : String = ""
var bssid : String = ""
var state : Number = 0
var last_state : Number = 10
var area : String = ""


class MainActivity : ComponentActivity(), SensorEventListener, TextToSpeech.OnInitListener {
    private var tts: TextToSpeech? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        //Beginning Permissions Check
        ctx = applicationContext

        // Initialize the TextToSpeech engine.
        tts = TextToSpeech(this, this)

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

//        tts!!.speak("hello world hahahahaa i am tired of this not working", TextToSpeech.QUEUE_FLUSH, null, null)

        setContent {
            MagnetometerTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {
                    Greeting("Android")
//                    ButtonScreen()
                    ButtonScreen { getWifiInfo(it)}
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
            results = (wifiManager.scanResults).toString()
            wifiInfo = (wifiManager.connectionInfo).toString()
//            Log.d(TAG, wifiInfo)
            bssid = wifiInfo.substringAfter("BSSID: ").substringBefore(", ")

            if (bssid == "78:67:0e:3a:25:3a"){
                area = "IT3 House"
            }
            Log.d(TAG, bssid)
            Log.d(TAG, area)
            speakIt(area)
//            val bssid = wifiInfo.bssid
//            Log.d(TAG, "getWifiInfo: BSSID=$bssid")
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

            val vibrator = getSystemService(Vibrator::class.java)
//            Log.d(TAG, "z: ${ztoDegrees}")

//            if (ztoDegrees in 47.0..51.0){
//                state = 1; //bathroom
//            }
//            else if (ztoDegrees in -27.0..-30.0){
//                state = 2; //door
//            }
//            else if (ztoDegrees in -20.0..-24.0){
//                state = 3; //apoorva's room
//            }
//            else if (ztoDegrees in -52.0..-56.0){
//                state = 4; //neeti's room
//            }

            if (ztoDegrees >= -51.0 && ztoDegrees <= -47.0) {
                state = 1 // bathroom
            } else if (ztoDegrees >= -30.0 && ztoDegrees <= -27.0) {
                state = 2 // door
            } else if (ztoDegrees >= -24.0 && ztoDegrees <= -20.0) {
                state = 3 // apoorva's room
            } else if (ztoDegrees >= -56.0 && ztoDegrees <= -52.0) {
                state = 4 // neeti's room
            }
            else{
                state = 0
            }

            if (state == 1 && last_state != 1 ){
                Log.d(TAG, "bathroom z: ${ztoDegrees}")
                vibrator.vibrate(
                    VibrationEffect.createOneShot(
                        50,
                        VibrationEffect.DEFAULT_AMPLITUDE
                    )
                )
                speakIt("bathroom")
                last_state = 1
            }
            else if (state == 2 && last_state != 2 ){
                Log.d(TAG, "door z: ${ztoDegrees}")
                vibrator.vibrate(
                    VibrationEffect.createOneShot(
                        50,
                        VibrationEffect.DEFAULT_AMPLITUDE
                    )
                )
                speakIt("door")
                last_state = 2
            }
            else if (state == 3 && last_state != 3 ){
                Log.d(TAG, "apoorva's room z: ${ztoDegrees}")
                vibrator.vibrate(
                    VibrationEffect.createOneShot(
                        50,
                        VibrationEffect.DEFAULT_AMPLITUDE
                    )
                )
                speakIt("apoorva's room")
                last_state = 3
            }
            else if (state == 4 && last_state != 4 ){
                Log.d(TAG, "neeti's room z: ${ztoDegrees}")
                vibrator.vibrate(
                    VibrationEffect.createOneShot(
                        50,
                        VibrationEffect.DEFAULT_AMPLITUDE
                    )
                )
                speakIt("neeti's room")
                last_state = 4
            }
//            else{
//                last_state = 0
//            }
        }
    }

    fun speakIt(str: String){
        tts?.speak(str,QUEUE_FLUSH, null, "")
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

    override fun onInit(status: Int) {
//        TODO("Not yet implemented")
        // Check if the initialization was successful.
        if (status == TextToSpeech.SUCCESS) {
            // Set the language to US English.
            tts?.language = Locale.US
        } else {
            // Log an error message.
            Log.e("TTS", "Initialization failed.")
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
fun ButtonScreen(onButtonClick: (Context) -> Unit) {
    val context = LocalContext.current

    Column(
        modifier = Modifier.padding(top = 36.dp)
    ) {
        Button(onClick = { onButtonClick(context) }) {
            Text("Get Wifi Info")
        }
    }
}

@Composable
fun Greeting(name: String) {
    Text(text = bssid)
//    Text(text = area)
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    MagnetometerTheme {
        Greeting("Android")
    }
}
package com.example.magnetometer

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorManager
import android.util.Log

// To isolate the log output so that only statements in this class are shown type "tag=:SensorRead" in the filter
private const val TAG = "MAIN"

/*Function for Identifying Sensors
https://developer.android.com/guide/topics/sensors/sensors_overview#sensors-identify
*/

fun listSensors(sensorManager: SensorManager){
    /*List all of the Sensors on this device
     returns a List that contains the type 'Sensor'
     */
    val deviceSensors: List<Sensor> = sensorManager.getSensorList(Sensor.TYPE_ALL)
    /*
    Use the List's Iterator method to write all the sensors to the log
    .forEach method uses 'it' to represent the value at the iterator's index
     https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/for-each.html#foreach
     */
    deviceSensors.forEach {
        // the '$' inside the quotes allows one to insert a variable into the string
        // String Templates - https://kotlinlang.org/docs/strings.html#string-templates
        Log.d(TAG, "Device Sensor:$it ")
    }

    // Flow for Identifiying Sensors
    // https://developer.android.com/guide/topics/sensors/sensors_overview#sensors-identify
    // SensorManager is still initialized in the MainActivity
    // sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
}

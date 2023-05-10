package com.example.magnetometer

/*  NOTE: This is not a class and as such Kotlin will include any code in the same package without
the need for an explicit import
 */

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import androidx.activity.result.ActivityResultLauncher
import androidx.core.content.ContextCompat



var permissionsList: ArrayList<String>? = null
var permissionsStr = arrayOf(
    Manifest.permission.ACCESS_COARSE_LOCATION,
    Manifest.permission.ACCESS_FINE_LOCATION,
    Manifest.permission.ACCESS_NETWORK_STATE,
    Manifest.permission.ACCESS_WIFI_STATE
)
var permissionsCount = 0
var permissionsResultLauncher: ActivityResultLauncher<Array<String>>? = null
//var isLocCoarsePermissionGranted = false
var isLocFinePermissionGranted = false
var isNetworkPermissionGranted = false
var isWifiPermissionGranted = false

fun requestPermission(ctx :Context) {
    isLocFinePermissionGranted = ContextCompat.checkSelfPermission(
        ctx,
        Manifest.permission.ACCESS_FINE_LOCATION
    ) == PackageManager.PERMISSION_GRANTED

    isNetworkPermissionGranted = ContextCompat.checkSelfPermission(
        ctx,
        Manifest.permission.ACCESS_NETWORK_STATE
    ) == PackageManager.PERMISSION_GRANTED

    isWifiPermissionGranted = ContextCompat.checkSelfPermission(
        ctx,
        Manifest.permission.ACCESS_WIFI_STATE
    ) == PackageManager.PERMISSION_GRANTED

    val permissionsRequest: MutableList<String> = ArrayList()

    if (!isLocFinePermissionGranted) {
        permissionsRequest.add(Manifest.permission.ACCESS_FINE_LOCATION)
    }

    if (!isNetworkPermissionGranted) {
        permissionsRequest.add(Manifest.permission.ACCESS_NETWORK_STATE)
    }

    if (!isWifiPermissionGranted) {
        permissionsRequest.add(Manifest.permission.ACCESS_WIFI_STATE)
    }

    if (!permissionsRequest.isEmpty()) {
        permissionsResultLauncher?.launch(permissionsRequest.toTypedArray())
    }
}
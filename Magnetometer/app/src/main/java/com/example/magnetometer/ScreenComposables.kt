package com.example.magnetometer

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.magnetometer.ui.theme.MagnetometerTheme

private const val TAG = "SCREEN"

class MainApplicationViewModel() : ViewModel() {
    //Create mutableState of type FLoatArray for holding SensorData
    val sensorValue = mutableStateOf((floatArrayOf(1f, 1f, 1f)))
    val scope = CoroutineScope(Job() + Dispatchers.Default)
    fun updateSensorValue(sensorFloatArray: FloatArray) {
        scope.launch {
            sensorValue.value = sensorFloatArray
        }

    }
}

@Composable
fun MainApplicationScreen(
    modifier: Modifier,
    mainApplicationViewModel: MainApplicationViewModel = viewModel(),
    time : Int
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
            mainApplicationViewModel,
            time)
    }
}

@Composable
fun SensorValueDisplay(modifier: Modifier,
                       mainApplicationViewModel: MainApplicationViewModel,
                       time: Int
){

    val sensorValues  by mainApplicationViewModel.sensorValue
    Column (
        modifier.padding(8.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ){
        Text(text = "X: ${sensorValues?.get(0)}")
        Text(text = "Y: ${sensorValues?.get(1)}")
        Text(text = "Z: ${sensorValues?.get(2)}")
        Text(text = "Tick: $time")
    }
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    MagnetometerTheme {
        MainApplicationScreen()
    }
}
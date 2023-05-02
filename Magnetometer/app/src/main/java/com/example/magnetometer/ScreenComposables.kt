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
package com.example.parkingslot

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.parkingslot.Route.MyAppNavigation
import com.example.parkingslot.ui.theme.ParkingslotTheme


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            startUp();
        }
    }
}

@Composable
fun startUp(modifier: Modifier = Modifier) {
    MyAppNavigation()
}

@Preview(showBackground = true)
@Composable
fun startUpPreview() {
    ParkingslotTheme {
        startUp()
    }
}
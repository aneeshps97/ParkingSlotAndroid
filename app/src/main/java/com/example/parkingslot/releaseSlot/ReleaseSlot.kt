package com.example.parkingslot.releaseSlot

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import com.example.parkingslot.background.PageBackground

@Composable
fun ReleaseSlot(
    modifier: Modifier = Modifier,
    navController: NavController
) {
    PageBackground() {
        Text("ReleaseSlot")
    }
}
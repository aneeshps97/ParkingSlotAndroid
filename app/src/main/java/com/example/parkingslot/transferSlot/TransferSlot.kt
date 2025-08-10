package com.example.parkingslot.transferSlot

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import com.example.parkingslot.background.PageBackground

@Composable
fun TransferSlot(
    modifier: Modifier = Modifier,
    navController: NavController
) {
    PageBackground() {
        Text("TransferSlot")
    }
}
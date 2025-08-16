package com.example.parkingslot.mainpages.transferSlot

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import com.example.parkingslot.mainpages.background.PageBackground

@Composable
fun TransferSlot(
    modifier: Modifier = Modifier,
    navController: NavController
) {
    PageBackground() {
        Text("TransferSlot")
    }
}
package com.example.parkingslot.buttons

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun LogOutButton(onClick: () -> Unit = {}) {
    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        IconButton(
            onClick = onClick,
            modifier = Modifier
                .padding(16.dp)
                .align(Alignment.TopEnd)
                .background(
                    color = Color.White, // Or any color you prefer
                    shape = CircleShape
                ).border(2.dp, Color.Black, shape = CircleShape)
                .size(48.dp) // Size to give space for circle
        ) {
            Icon(
                imageVector = Icons.Default.AccountCircle,
                contentDescription = "Back",
                tint = Color.Black // Icon color
            )
        }
    }
}
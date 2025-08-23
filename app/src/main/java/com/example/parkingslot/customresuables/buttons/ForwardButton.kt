package com.example.parkingslot.customresuables.buttons

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun ForwardButton(
    isEnabled: Boolean = false,
    onClick: () -> Unit = {},
    alignment: Alignment = Alignment.Center // Default alignment
) {
    val color: Color = if (isEnabled) Color.Black else Color.LightGray

    Box(
        modifier = Modifier.fillMaxWidth(),
        contentAlignment = alignment
    ) {
        IconButton(
            onClick = onClick,
            enabled = isEnabled,
            modifier = Modifier
                .padding(16.dp)
                .background(
                    color = Color(0xFFEEEFEE),
                    shape = CircleShape
                )
                .border(2.dp, color, shape = CircleShape)
                .size(48.dp)
        ) {
            Icon(
                imageVector = Icons.Default.ArrowForward,
                contentDescription = "Forward",
                tint = color
            )
        }
    }
}

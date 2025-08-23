package com.example.parkingslot.customresuables.textfields

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun InputTextFieldWithButton(
    value: String,
    onValueChange: (String) -> Unit,
    onAddClick: () -> Unit,
    labelText: String
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(labelText) },
        modifier = Modifier
            .padding(end = 8.dp),
        trailingIcon = {
            IconButton(
                onClick = onAddClick,
                modifier = Modifier
                    .background(Color.Black, shape = RoundedCornerShape(8.dp))
                    .size(36.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Add Slot",
                    tint = Color.White
                )
            }
        },
        singleLine = true
    )
}
package com.example.parkingslot.customresuables.labels

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun LabelWithTrailingIcon(
    value: String,
    onRemoveClick: () -> Unit,
    showEdit: Boolean = false,            // 👈 new parameter with default false
    onEditClick: (() -> Unit)? = null     // 👈 optional callback for edit'
) {
    Row(
        modifier = Modifier
            // Removed .fillMaxWidth() to allow the Row to be centered by its parent
            .padding(end = 8.dp)
            .background(Color(0xFFF0F0F0), shape = RoundedCornerShape(8.dp))
            .border(
                width = 1.dp,
                color = Color.Gray,
                shape = RoundedCornerShape(8.dp)
            )
            .padding(horizontal = 12.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = value,
                style = MaterialTheme.typography.bodyLarge
            )
        }

        // ✅ Show edit button only when needed
        if (showEdit) {
            IconButton(
                onClick = { onEditClick?.invoke() },
                modifier = Modifier
                    .background(Color.Gray, shape = RoundedCornerShape(6.dp))
                    .size(28.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Edit,
                    contentDescription = "Edit",
                    tint = Color.White
                )
            }
        }
        Spacer(modifier = Modifier.width(10.dp))

        IconButton(
            onClick = onRemoveClick,
            modifier = Modifier
                .background(Color.Black, shape = RoundedCornerShape(6.dp))
                .size(28.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Close,
                contentDescription = "Remove",
                tint = Color.White
            )
        }
    }
}
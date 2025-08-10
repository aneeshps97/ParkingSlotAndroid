package com.example.parkingslot.confirm

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable

@Composable
fun ConfirmPopUp(
    showDialog: Boolean,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit
){

    if (showDialog) {
        AlertDialog(
            onDismissRequest = onDismiss,
            title = {
                Text(text = "Confirm Action")
            },
            text = {
                Text("Are you sure you want to proceed?")
            },
            confirmButton = {
                TextButton(onClick = onConfirm) {
                    Text("Confirm")
                }
            },
            dismissButton = {
                TextButton(onClick = onDismiss) {
                    Text("Cancel")
                }
            }
        )
    }
}
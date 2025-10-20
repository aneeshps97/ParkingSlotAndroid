package com.example.parkingslot.customresuables.popUp

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.navigation.NavController
import com.example.parkingslot.webConnect.dto.slot.SlotData
import com.example.parkingslot.webConnect.dto.user.UserData

@Composable
fun EditSlotListPopUp(
    showDialog: Boolean,
    onDismiss: () -> Unit,
    navController: NavController,
    slotList: SnapshotStateList<SlotData>, // Add this parameter to pass the list
    selectedSlots: SnapshotStateList<SlotData>
) {
    if (showDialog) {
        Dialog(onDismissRequest = onDismiss) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp)
                    // Added a shadow for elevation
                    .shadow(
                        elevation = 8.dp,
                        shape = RoundedCornerShape(12.dp)
                    )
                    .background(Color.White, shape = RoundedCornerShape(12.dp))
                    .border(
                        width = 2.dp,
                        color = Color.Black,
                        shape = RoundedCornerShape(10.dp)
                    )
                    .padding(16.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(max = 400.dp), // Limit height for scroll
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Text(
                        fontSize = 14.sp,
                        text = "Select Slots",
                        style = MaterialTheme.typography.titleMedium,
                        color = Color.Black,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )

                    // Scrollable list of buttons
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f)
                            .padding(vertical = 8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        items(slotList) { slotData ->
                            Button(
                                onClick = {
                                    slotData.slotId.let {
                                        if(selectedSlots.contains(slotData)){
                                            selectedSlots.remove(slotData)
                                        }else{
                                            selectedSlots.add(slotData)
                                        }
                                    }
                                },
                                modifier = Modifier
                                    .fillMaxWidth(0.8f)
                                    .height(48.dp)
                                    .then(
                                        if (selectedSlots.contains(slotData)) {
                                            Modifier.border(2.dp, Color.Black, RoundedCornerShape(12.dp))
                                        } else {
                                            Modifier
                                        }
                                    ),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = Color(0xFF707072),
                                    contentColor = Color.White
                                ),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Text(slotData.name)
                            }
                        }
                    }


                    TextButton(onClick = onDismiss) {
                        Text("Finish", color = Color.Black)
                    }
                }
            }
        }
    }
}
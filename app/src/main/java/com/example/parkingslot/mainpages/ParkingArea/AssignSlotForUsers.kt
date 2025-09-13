package com.example.parkingslot.mainpages.ParkingArea


import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.content.Context
import android.net.Uri
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.updateTransition
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.autoSaver
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.parkingslot.Route.Routes
import com.example.parkingslot.mainpages.background.PageBackground
import com.example.parkingslot.webConnect.dto.booking.BookingRequest
import com.example.parkingslot.webConnect.dto.booking.BookingResponse
import com.example.parkingslot.webConnect.dto.parkingArea.ParkingAreaData
import com.example.parkingslot.webConnect.dto.slot.SlotData
import com.example.parkingslot.webConnect.dto.user.UserData
import com.example.parkingslot.webConnect.retrofit.ParkingSlotApi
import com.example.parkingslot.webConnect.retrofit.RetrofitService
import com.google.gson.Gson
import java.time.LocalDate
import java.time.format.DateTimeFormatter


@Composable
fun AssignSlotForUsers(
    navController: NavController, modifier: Modifier = Modifier,
    parkingAreaData: ParkingAreaData
) {
    val context: Context = LocalContext.current
    val users: MutableList<UserData> = parkingAreaData.users.toMutableList()
    val slots: MutableList<SlotData> = parkingAreaData.slots.toMutableList()

    val userId: Int
    val parkingAreaId: Int = parkingAreaData.parkingAreaId
    val date: String
    val slotId: Int
    var selectedUser by remember { mutableStateOf<UserData?>(null) }
    var selectedSlot by remember { mutableStateOf<SlotData?>(null) }
    var selectedDate by remember { mutableStateOf(LocalDate.now()) }


    PageBackground {

        Box(modifier = Modifier.fillMaxSize()) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.TopCenter),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    parkingAreaData.name.toUpperCase(),
                    style = MaterialTheme.typography.headlineMedium
                )
                Spacer(modifier = Modifier.height(30.dp))

                Text(
                    text = "BOOK SLOTS",
                    style = MaterialTheme.typography.headlineMedium.copy(fontSize = 20.sp)
                )
                Divider(
                    modifier = Modifier
                        .fillMaxWidth(0.4f)
                        .height(1.5.dp), color = Color.Black
                )
                Spacer(modifier = Modifier.height(16.dp))



                MinimalistDatePicker(
                    selectedDate = selectedDate,
                    onDateSelected = { selectedDate = it }
                )


                AnimatedUserDropdown(
                    users = users,
                    selectedUser = selectedUser,
                    onUserSelected = { selectedUser = it }
                )

                AnimatedSlotDropdown(
                    slots = slots,
                    selectedSlot = selectedSlot,
                    onSlotSelected = { selectedSlot = it }
                )

                BookButton({
                    val api = RetrofitService.getRetrofit().create(ParkingSlotApi::class.java)
                    val bookingRequest = BookingRequest(
                        slotId = selectedSlot?.slotId ?: 0,
                        date = selectedDate.toString(),
                        userId = selectedUser?.userId ?: 0,
                        parkingAreaId = Integer.valueOf(parkingAreaData.parkingAreaId)
                    )
                    api.assignSlotsToUser(bookingRequest).enqueue(object :
                        retrofit2.Callback<BookingResponse> {
                        override fun onResponse(
                            call: retrofit2.Call<BookingResponse>,
                            response: retrofit2.Response<BookingResponse>
                        ) {
                            if (response.body() != null && response.body()?.status == 0) {
                                Toast.makeText(
                                    context, "" + response.body()?.message, Toast.LENGTH_SHORT
                                ).show()
                            } else {
                                Toast.makeText(
                                    context, "" + response.body()?.message, Toast.LENGTH_SHORT
                                ).show()
                            }
                        }

                        override fun onFailure(
                            call: retrofit2.Call<BookingResponse>,
                            t: Throwable
                        ) {
                            Toast.makeText(context, "Error: ${t.message}", Toast.LENGTH_SHORT)
                                .show()
                        }
                    })


                })

                val json = Uri.encode(Gson().toJson(parkingAreaData))
                AutoAssignSlots({navController.navigate(Routes.autoAssignSlots+"/$json")})

                FinishButton({
                    navController.navigate(Routes.viewYourParkingAreas)
                })


            }
        }

    }


}

@Composable
fun AnimatedUserDropdown(
    users: MutableList<UserData>,
    selectedUser: UserData?,
    onUserSelected: (UserData) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    val transition = updateTransition(targetState = expanded, label = "DropdownTransition")
    val arrowRotation by transition.animateFloat(label = "ArrowRotation") { isExpanded ->
        if (isExpanded) 180f else 0f
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Text(
            text = "User Selection",
            fontSize = 20.sp,
            color = Color.Black,
            modifier = Modifier.padding(5.dp)
        )

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, Color.Black, shape = MaterialTheme.shapes.small)
                .clickable { expanded = !expanded }
                .padding(horizontal = 16.dp, vertical = 12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = selectedUser?.name?.takeIf { it.isNotEmpty() } ?: "Select a user",
                    fontSize = 30.sp,
                    color = Color.Black
                )
                Icon(
                    imageVector = Icons.Default.ArrowDropDown,
                    contentDescription = "Dropdown Arrow",
                    modifier = Modifier.rotate(arrowRotation),
                    tint = Color.Black
                )
            }
        }

        AnimatedVisibility(visible = expanded) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = 200.dp) // Enables scrolling when needed
                    .border(1.dp, Color.Black, shape = MaterialTheme.shapes.small)
                    .background(Color.White)
            ) {
                LazyColumn {
                    items(users) { user ->
                        Column {
                            Text(
                                text = user.name,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        onUserSelected(user)
                                        expanded = false
                                    }
                                    .padding(12.dp),
                                fontSize = 16.sp,
                                color = Color.Black
                            )
                            Divider(color = Color.LightGray, thickness = 0.5.dp)
                        }
                    }
                }
            }
        }
    }
}


@Composable
fun AnimatedSlotDropdown(
    slots: List<SlotData>,
    selectedSlot: SlotData?,
    onSlotSelected: (SlotData) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    val transition = updateTransition(targetState = expanded, label = "DropdownTransition")

    val arrowRotation by transition.animateFloat(label = "ArrowRotation") { isExpanded ->
        if (isExpanded) 180f else 0f
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Text(
            text = "Slot Selection",
            fontSize = 20.sp,
            color = Color.Black,
            modifier = Modifier.padding(5.dp)
        )

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, Color.Black, shape = MaterialTheme.shapes.small)
                .clickable { expanded = !expanded }
                .padding(horizontal = 16.dp, vertical = 12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = selectedSlot?.name?.takeIf { it.isNotEmpty() } ?: "Select a slot",
                    fontSize = 30.sp,
                    color = Color.Black
                )
                Icon(
                    imageVector = Icons.Default.ArrowDropDown,
                    contentDescription = "Dropdown Arrow",
                    modifier = Modifier.rotate(arrowRotation),
                    tint = Color.Black
                )
            }
        }

        AnimatedVisibility(visible = expanded) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = 200.dp) // Limit height for scroll
                    .border(1.dp, Color.Black, shape = MaterialTheme.shapes.small)
                    .background(Color.White)
            ) {
                LazyColumn {
                    items(slots) { slot ->
                        Column {
                            Text(
                                text = slot.name,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        onSlotSelected(slot)
                                        expanded = false
                                    }
                                    .padding(12.dp),
                                fontSize = 16.sp,
                                color = Color.Black
                            )
                            Divider(color = Color.LightGray, thickness = 0.5.dp)
                        }
                    }
                }
            }
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("RememberReturnType")
@Composable
fun MinimalistDatePicker(
    selectedDate: LocalDate,
    onDateSelected: (LocalDate) -> Unit
) {
    val context = LocalContext.current
    val datePickerDialog = remember {
        DatePickerDialog(
            context,
            { _, year, month, dayOfMonth ->
                onDateSelected(LocalDate.of(year, month + 1, dayOfMonth))
            },
            selectedDate.year,
            selectedDate.monthValue - 1,
            selectedDate.dayOfMonth
        )
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .clickable { datePickerDialog.show() }
            .border(1.dp, Color.Black, shape = MaterialTheme.shapes.small)
            .padding(horizontal = 16.dp, vertical = 16.dp)
    ) {
        Row {
            Column {
                Icon(
                    imageVector = Icons.Default.DateRange,
                    contentDescription = "Calendar Icon",
                    tint = Color.Black,
                    modifier = Modifier.size(70.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
            }
            Column {
                Row {
                    Text(
                        text = "Date",
                        fontSize = 20.sp,
                        color = Color.Black,
                        modifier = Modifier.padding(5.dp)
                    )

                }
                Row {
                    Text(
                        text = selectedDate.format(DateTimeFormatter.ofPattern("dd MMM yyyy")),
                        fontSize = 25.sp,
                        color = Color.Black
                    )
                }
            }
        }
    }
}


@Composable
fun BookButton(onClick: () -> Unit) {
    Button(
        onClick = onClick,
        colors = ButtonDefaults.buttonColors(
            containerColor = Color.Black,
            contentColor = Color.White
        ),
        shape = RoundedCornerShape(4.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
    ) {
        Text(
            text = "Book",
            fontSize = 30.sp,
            color = Color.White
        )
    }
}

@Composable
fun AutoAssignSlots(onClick: () -> Unit) {
    OutlinedButton(
        onClick = onClick,
        colors = ButtonDefaults.outlinedButtonColors(
            containerColor = Color.LightGray,
            contentColor = Color.White
        ),
        shape = RoundedCornerShape(4.dp),
        border = BorderStroke(1.dp, Color.Black),
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Text(
            text = "Smart Book",
            fontSize = 30.sp,
            color = Color.Black
        )
    }
}

@Composable
fun FinishButton(onClick: () -> Unit) {
    OutlinedButton(
        onClick = onClick,
        colors = ButtonDefaults.outlinedButtonColors(
            contentColor = Color.Black
        ),
        shape = RoundedCornerShape(4.dp),
        border = BorderStroke(1.dp, Color.Black),
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Text(
            text = "Finish",
            fontSize = 30.sp,
            color = Color.Black
        )
    }
}





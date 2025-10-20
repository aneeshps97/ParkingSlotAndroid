package com.example.parkingslot.mainpages.ParkingArea

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.motionEventSpy
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.parkingslot.Route.Routes
import com.example.parkingslot.customresuables.popUp.EditSlotListPopUp
import com.example.parkingslot.customresuables.popUp.EditUsersListPopUp
import com.example.parkingslot.customresuables.popUp.TransferAdminPopUP
import com.example.parkingslot.mainpages.background.PageBackground
import com.example.parkingslot.webConnect.dto.booking.AutoAssignRequest
import com.example.parkingslot.webConnect.dto.booking.BookingRequest
import com.example.parkingslot.webConnect.dto.booking.BookingResponse
import com.example.parkingslot.webConnect.dto.parkingArea.ParkingAreaData
import com.example.parkingslot.webConnect.dto.slot.SlotData
import com.example.parkingslot.webConnect.dto.user.UserData
import com.example.parkingslot.webConnect.retrofit.ParkingSlotApi
import com.example.parkingslot.webConnect.retrofit.RetrofitService
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.time.LocalDate

@Composable
fun AutoAssignSlots(
    navController: NavController, modifier: Modifier = Modifier,
    parkingAreaData: ParkingAreaData
) {
    val context: Context = LocalContext.current
    val users: MutableList<UserData> = parkingAreaData.users.toMutableList()
    val slots: MutableList<SlotData> = parkingAreaData.slots.toMutableList()
    val listOfUsers = remember(parkingAreaData) {
        mutableStateListOf<UserData>().apply { addAll(parkingAreaData.users) }
    }

    val listOfSlots = remember(parkingAreaData) {
        mutableStateListOf<SlotData>().apply { addAll(parkingAreaData.slots) }
    }

    val selectedUsers = remember { mutableStateListOf<UserData>() }
    val selectedSlots = remember { mutableStateListOf<SlotData>() }


    var selectedStartDate by remember { mutableStateOf(LocalDate.now()) }
    var selectedEndDate by remember { mutableStateOf(LocalDate.now()) }
    var frequency by remember { mutableStateOf("1") }
    var showEditUserListPopUp by remember { mutableStateOf(false) }
    var showEditSlotListPopUp by remember { mutableStateOf(false) }

    val validationSuccess by remember {
        derivedStateOf {
            selectedSlots.isNotEmpty() &&
                    selectedUsers.isNotEmpty() &&
                    frequency.toInt() > 0
        }
    }

    EditUsersListPopUp(
        showDialog = showEditUserListPopUp,
        onDismiss = { showEditUserListPopUp = false },
        navController = navController,
        userList = listOfUsers,
        selectedUsers = selectedUsers
    )

    EditSlotListPopUp(
        showDialog = showEditSlotListPopUp,
        onDismiss = { showEditSlotListPopUp = false },
        navController = navController,
        slotList = listOfSlots,
        selectedSlots = selectedSlots,
    )
    PageBackground {
        Box(modifier = Modifier.fillMaxSize()) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.TopCenter),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.height(30.dp))
                Text(
                    parkingAreaData.name.toUpperCase(),
                    style = MaterialTheme.typography.headlineMedium
                )
                Spacer(modifier = Modifier.height(30.dp))
                Text(
                    text = "AUTO ASSIGN",
                    style = MaterialTheme.typography.headlineMedium.copy(fontSize = 20.sp)
                )
                Divider(
                    modifier = Modifier
                        .fillMaxWidth(0.4f)
                        .height(1.5.dp), color = Color.Black
                )
                Spacer(modifier = Modifier.height(16.dp))


                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp), contentAlignment = Alignment.CenterStart
                ) {
                    Text(
                        "StartDate",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
                MinimalistDatePicker(
                    selectedDate = selectedStartDate,
                    onDateSelected = { selectedStartDate = it }
                )

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp), contentAlignment = Alignment.CenterStart
                ) {
                    Text(
                        "EndDate",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }

                MinimalistDatePicker(
                    selectedDate = selectedEndDate,
                    onDateSelected = { selectedEndDate = it }
                )

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp), contentAlignment = Alignment.CenterStart
                ) {
                    Text(
                        "Frequency",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
                Spacer(modifier = Modifier.height(10.dp))
                Box(modifier.padding(horizontal = 16.dp)) {
                    OutlinedTextField(
                        value = frequency,
                        onValueChange = { frequency = it },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        placeholder = { Text("1") },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = Color.White,
                            unfocusedContainerColor = Color.White,
                            focusedBorderColor = Color.Gray,
                            unfocusedBorderColor = Color.LightGray
                        ),
                        shape = RoundedCornerShape(8.dp),
                        keyboardOptions = KeyboardOptions.Default.copy(
                            keyboardType = KeyboardType.Number
                        )
                    )

                }
                Spacer(modifier.height(10.dp))
                SelectSlotsForSmartBooking({ showEditSlotListPopUp = true })
                Spacer(modifier.height(10.dp))
                SelectUsersForSmartBooking({ showEditUserListPopUp = true })
                Spacer(modifier.height(10.dp))
                BookButton(isenabled = validationSuccess, onClick = {
                    val api = RetrofitService.getRetrofit().create(ParkingSlotApi::class.java)
                    //create the slotIdList from the parking area data provided
                    //create the userIdList from the parking area data provided
                    //update select the data using a selector just like the
                    val userIds: List<Int> = selectedUsers.map { it.userId }
                    val slotIds: List<Int> = selectedSlots.map { it.slotId }

                    val autoAssignRequest: AutoAssignRequest = AutoAssignRequest(
                        parkingAreaId = parkingAreaData.parkingAreaId,
                        userIds = userIds,
                        slotIds = slotIds,
                        startDate = selectedStartDate.toString(),
                        endDate = selectedEndDate.toString(),
                        frequency = Integer.parseInt(frequency)
                    )
                    api.autoAssignSlots(autoAssignRequest).enqueue(object :
                        Callback<BookingResponse> {
                        override fun onResponse(
                            call: Call<BookingResponse>,
                            response: Response<BookingResponse>
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
                            call: Call<BookingResponse>,
                            t: Throwable
                        ) {
                            Toast.makeText(context, "Error: ${t.message}", Toast.LENGTH_SHORT)
                                .show()
                        }
                    })


                })

                FinishButton(onClick = {
                    navController.navigate(Routes.viewYourParkingAreas)
                })


            }
        }
    }
}


@Composable
fun SelectUsersForSmartBooking(onClick: () -> Unit) {
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
            text = "Select Users",
            fontSize = 30.sp,
            color = Color.White
        )
    }
}


@Composable
fun SelectSlotsForSmartBooking(onClick: () -> Unit) {
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
            text = "Select Slots",
            fontSize = 30.sp,
            color = Color.White
        )
    }
}
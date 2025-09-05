package com.example.parkingslot.mainpages.ParkingArea

import android.content.Context
import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.parkingslot.Route.Routes
import com.example.parkingslot.customresuables.textfields.LabeledTextField
import com.example.parkingslot.mainpages.background.PageBackground
import com.example.parkingslot.webConnect.dto.parkingArea.ParkingAreaResponse
import com.example.parkingslot.webConnect.dto.user.User
import com.example.parkingslot.webConnect.retrofit.ParkingSlotApi
import com.example.parkingslot.webConnect.retrofit.RetrofitService
import com.google.gson.Gson
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.OutlinedTextFieldDefaults
import com.example.parkingslot.webConnect.dto.booking.BookingData
import com.example.parkingslot.webConnect.dto.booking.BookingResponse
import com.example.parkingslot.webConnect.repository.BookingRepository
import com.example.parkingslot.webConnect.repository.ParkingAreaRepository

@Composable
fun EditParkingArea(
    navController: NavController,
    modifier: Modifier = Modifier,
    parkingAreaId: String?,
    parkingAreaName: String?
) {
    var name by remember { mutableStateOf(parkingAreaName) }
    val context: Context = LocalContext.current
    var parkingAreaRepository: ParkingAreaRepository = ParkingAreaRepository()
    var bookingRepository = BookingRepository()
    PageBackground {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            Box(modifier = Modifier.fillMaxSize()) {
                Text(
                    "EDIT",
                    modifier = Modifier
                        .align(Alignment.TopCenter)
                        .padding(top = 16.dp)
                )

                Box(
                    modifier = Modifier
                        .align(Alignment.Center)
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Column {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Box(modifier = Modifier.weight(1f)) {
                                OutlinedTextField(
                                    value = name.toString(),
                                    onValueChange = { name = it },
                                    modifier = modifier,
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedBorderColor = Color.DarkGray,
                                        unfocusedBorderColor = Color.Black,
                                    )
                                )
                            }

                            Button(
                                onClick = {
                                    handleChangeNameOfParkingArea(
                                        name.toString(),
                                        parkingAreaId,
                                        context,
                                        parkingAreaRepository
                                    )
                                },
                                modifier = Modifier
                                    .height(56.dp) // Match text field height
                                    .align(Alignment.CenterVertically),
                                shape = RoundedCornerShape(8.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = Color.Black,
                                    contentColor = Color.White
                                )
                            ) {
                                Icon(
                                    imageVector = Icons.Default.ArrowForward,
                                    contentDescription = "Edit"
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(20.dp)) // for horizontal spacing

                        Button(
                            onClick = {
                                handleEditSlotsClick(parkingAreaId.toString(), context, navController,parkingAreaRepository);
                            },
                            modifier = Modifier
                                .height(60.dp)
                                .fillMaxWidth(),
                            shape = RoundedCornerShape(16.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color.White,
                                contentColor = Color.Black
                            ),
                            border = BorderStroke(2.dp, Color.Black),
                            elevation = null
                        ) {
                            Text(
                                text = "Edit Slots",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }

                        Spacer(modifier = Modifier.height(20.dp)) // for horizontal spacing

                        Button(
                            onClick = {
                                handleEditUsersClick(
                                    parkingAreaId.toString(),
                                    context,
                                    navController,
                                    parkingAreaRepository
                                )
                            },
                            modifier = Modifier
                                .height(60.dp)
                                .fillMaxWidth(),
                            shape = RoundedCornerShape(16.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color.White,
                                contentColor = Color.Black
                            ),
                            border = BorderStroke(2.dp, Color.Black),
                            elevation = null
                        ) {
                            Text(
                                text = "Edit users",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }

                        Spacer(modifier = Modifier.height(20.dp)) // for horizontal spacing

                        Button(
                            onClick = {
                                handleGetCurrentBookingByParkingArea(
                                    context = context,
                                    parkingAreaId = Integer.parseInt(parkingAreaId),
                                    navController = navController,
                                    repository = bookingRepository
                                )
                            },
                            modifier = Modifier
                                .height(60.dp)
                                .fillMaxWidth(),
                            shape = RoundedCornerShape(16.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color.White,
                                contentColor = Color.Black
                            ),
                            border = BorderStroke(2.dp, Color.Black),
                            elevation = null
                        ) {
                            Text(
                                text = "Edit Booking",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }

                    }
                }

                Box(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(bottom = 32.dp)
                ) {
                    Button(
                        onClick = { navController.navigate(Routes.homePage) },
                        modifier = Modifier
                            .height(60.dp)
                            .fillMaxWidth(0.9f),
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color.White,
                            contentColor = Color.Black
                        ),
                        border = BorderStroke(2.dp, Color.Black),
                        elevation = null
                    ) {
                        Text(
                            text = "Finish",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }
        }
    }
}

fun handleGetCurrentBookingByParkingArea(
    context: Context,
    parkingAreaId: Int,
    navController: NavController,
    repository: BookingRepository
) {
    repository.getCurrentBookingByParkingArea(parkingAreaId) { result ->
        result.onSuccess { bookedSlots ->
            val json = Uri.encode(Gson().toJson(bookedSlots))
            navController.navigate("${Routes.editBooking}/$json/$parkingAreaId")

            if (bookedSlots.isEmpty()) {
                Toast.makeText(context, "No bookings available", Toast.LENGTH_SHORT).show()
            }
        }
        result.onFailure { error ->
            val json = Uri.encode(Gson().toJson(emptyList<BookingData>()))
            navController.navigate("${Routes.editBooking}/$json/$parkingAreaId")
            Toast.makeText(
                context,
                error.message ?: "Failed to find data",
                Toast.LENGTH_SHORT
            ).show()
        }
    }
}

fun handleEditUsersClick(
    parkingAreaId: String,
    context: Context,
    navController: NavController,
    repository: ParkingAreaRepository
) {
    repository.findParkingAreaById(parkingAreaId) { result ->
        result.onSuccess { response ->
            val json = Uri.encode(Gson().toJson(response.data))
            navController.navigate("${Routes.editUsers}/$json")
        }

        result.onFailure { error ->
            Toast.makeText(
                context,
                 "Data not found",
                Toast.LENGTH_SHORT
            ).show()
        }
    }
}


fun handleEditSlotsClick(
    parkingAreaId: String,
    context: Context,
    navController: NavController,
    repository: ParkingAreaRepository
) {
    repository.findParkingAreaById(parkingAreaId) { result ->
        result.onSuccess { response ->
            val json = Uri.encode(Gson().toJson(response.data))
            navController.navigate("${Routes.editSlots}/$json")
        }

        result.onFailure { error ->
            Toast.makeText(
                context,
                 "Data not found",
                Toast.LENGTH_SHORT
            ).show()
        }
    }
}


fun handleChangeNameOfParkingArea(
    name: String,
    parkingAreaId: String?,
    context: Context,
    repository: ParkingAreaRepository
) {
    val id = parkingAreaId?.toIntOrNull()
    if (id == null) {
        Toast.makeText(context, "Invalid Parking Area ID", Toast.LENGTH_SHORT).show()
        return
    }

    repository.changeNameOfParkingArea(id, name) { result ->
        result.onSuccess {
            Toast.makeText(context, "Updated", Toast.LENGTH_SHORT).show()
        }
        result.onFailure { error ->
            Toast.makeText(
                context,
                error.message ?: "Failed to update parking area",
                Toast.LENGTH_SHORT
            ).show()
        }
    }
}


@Preview(showBackground = true)
@Composable
fun EditParkingAreaPreview() {
    // Dummy NavController for preview
    val dummyNavController = rememberNavController()
    EditParkingArea(
        navController = dummyNavController,
        modifier = Modifier.fillMaxSize(),
        parkingAreaId = "2",
        parkingAreaName = "abcde"
    )
}

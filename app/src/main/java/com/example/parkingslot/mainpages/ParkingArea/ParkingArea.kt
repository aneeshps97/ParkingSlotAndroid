package com.example.parkingslot.mainpages.ParkingArea

import android.content.Context
import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddChart
import androidx.compose.material.icons.filled.ConfirmationNumber
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.parkingslot.Route.Routes
import com.example.parkingslot.mainpages.background.PageBackground
import com.example.parkingslot.customresuables.confirm.ConfirmPopUp
import com.example.parkingslot.webConnect.dto.booking.BookingData
import com.example.parkingslot.webConnect.dto.parkingArea.ParkingAreaResponse
import com.example.parkingslot.webConnect.repository.BookingRepository
import com.example.parkingslot.webConnect.repository.ParkingAreaRepository
import com.example.parkingslot.webConnect.retrofit.ParkingSlotApi
import com.example.parkingslot.webConnect.retrofit.RetrofitService
import com.google.gson.Gson
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.time.LocalDate

@Composable
fun parkingArea(
    navController: NavController, modifier: Modifier = Modifier,
    parkingAreaId: String,
    parkingAreaName: String,
    ticketLine1:String,
    ticketLine2: String,
    adminId: String,
) {
    val context = LocalContext.current
    val sharedPref = remember { context.getSharedPreferences("loginPref", Context.MODE_PRIVATE) }
    val userId = sharedPref.getInt("user_id", 1)
    var showConfirmationDialogForDelete by remember { mutableStateOf(false) }
    var bookingRepository = BookingRepository()
    var parkingAreaRepository = ParkingAreaRepository()

    PageBackground {
        Box(modifier = Modifier.fillMaxSize()) {
            ConfirmPopUp(
                text1= "Delete $parkingAreaName",
                text2 = "Are you sure ?",
                showDialog = showConfirmationDialogForDelete,
                onDismiss = { showConfirmationDialogForDelete = false },
                onConfirm = {
                    handleDeleteParkingArea(
                        Integer.parseInt(parkingAreaId),
                        context,
                        navController
                    )
                    showConfirmationDialogForDelete = false
                }
            )
            Box(
                modifier = Modifier.fillMaxSize().padding(20.dp),
                contentAlignment = Alignment.TopCenter
            ) {
                Text(
                    parkingAreaName,
                    style = MaterialTheme.typography.headlineSmall.copy(
                        fontWeight = FontWeight.SemiBold
                    )
                )
            }
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .shadow(
                                elevation = 8.dp, // Adjust this value to change the shadow depth
                                shape = RoundedCornerShape(16.dp)
                            )
                            .background(Color.White, shape = RoundedCornerShape(16.dp))
                            .fillMaxWidth(0.9f) // The Box should fill the width to contain the buttons
                    ) {
                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Spacer(modifier = Modifier.height(16.dp))

                            DashboardButton(
                                text = "View Ticket",
                                icon = Icons.Default.ConfirmationNumber,
                                onClick = {
                                    handleBookingCheckForToday(
                                        parkingAreaId = parkingAreaId.toInt(),
                                        context = context,
                                        userId = userId,
                                        navController = navController,
                                        repository = bookingRepository
                                    )
                                }
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            DashboardButton("Available Slots", icon = Icons.Default.AddChart, onClick = {
                                handleGetFreeSlotsInParkingArea(
                                    userId = userId,
                                    parkingAreaId = Integer.parseInt(parkingAreaId),
                                    navController = navController,
                                    context = context,
                                    repository = parkingAreaRepository
                                )
                            })
                            Spacer(modifier = Modifier.height(16.dp))
                            DashboardButton("My Bookings", icon = Icons.Default.DateRange, onClick = {
                                handleGetCurrentBookingOfUser(
                                    userId = userId,
                                    parkingAreaId = Integer.parseInt(parkingAreaId),
                                    navController = navController,
                                    context = context,
                                    repository = bookingRepository
                                )
                            })

                            Spacer(modifier = Modifier.height(16.dp))

                        }
                    }

                    if (userId.toString() == adminId.toString()) {
                        Spacer(modifier = Modifier.height(16.dp))
                        Box(
                            modifier = Modifier
                                .shadow(
                                    elevation = 8.dp, // Adjust this value to change the shadow depth
                                    shape = RoundedCornerShape(16.dp)
                                )
                                .background(Color.White, shape = RoundedCornerShape(16.dp))
                                .fillMaxWidth(0.9f) // The Box should fill the width to contain the buttons
                        ) {
                            Column(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Spacer(modifier = Modifier.height(16.dp))
                                DashboardButton(
                                    "Delete",
                                    icon = Icons.Default.Delete,
                                    onClick = {
                                        showConfirmationDialogForDelete = true
                                    })
                                Spacer(modifier = Modifier.height(16.dp)) // Added space between buttons
                                DashboardButton(
                                    "Update",
                                    icon = Icons.Default.Edit,
                                    onClick = {
                                        navController.navigate(Routes.editParkingArea + "/" + parkingAreaId + "/" + parkingAreaName+"/"+ticketLine1+"/"+ticketLine2)
                                    })
                                Spacer(modifier = Modifier.height(16.dp))
                            }
                        }


                    }
                }
            }
        }
    }
}

fun handleDeleteParkingArea(parkingAreaId: Int, context: Context, navController: NavController) {
    try {
        val api = RetrofitService.getRetrofit().create(ParkingSlotApi::class.java)
        api.deleteParkingArea(parkingAreaId)
            .enqueue(object : Callback<ParkingAreaResponse> {
                override fun onResponse(
                    call: Call<ParkingAreaResponse?>,
                    response: Response<ParkingAreaResponse?>
                ) {
                    if (response.body()?.status == 0) {
                        Toast.makeText(context, "parking area deleted", Toast.LENGTH_SHORT).show()
                        navController.navigate(Routes.viewYourParkingAreas)
                    } else {
                        Toast.makeText(context, "failed", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(
                    call: Call<ParkingAreaResponse?>,
                    t: Throwable
                ) {
                    Toast.makeText(context, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
                }
            })
    } catch (e: Exception) {
        Toast.makeText(context, "Exception occurred: ${e.message}", Toast.LENGTH_SHORT).show()
    }
}

fun handleGetFreeSlotsInParkingArea(
    userId: Int,
    parkingAreaId: Int,
    navController: NavController,
    context: Context,
    repository: ParkingAreaRepository
) {
    try {
        repository.getFreeSlotsInParkingArea(parkingAreaId) { result ->
            result.onSuccess { bookingResponse ->
                val bookedSlots = bookingResponse.data ?: emptyList()
                val json = Uri.encode(Gson().toJson(bookedSlots))
                navController.navigate("${Routes.availableSlots}/$json/$parkingAreaId")
            }
            result.onFailure { error ->
                val emptyJson = Uri.encode(Gson().toJson(emptyList<BookingData>()))
                navController.navigate("${Routes.availableSlots}/$emptyJson/$parkingAreaId")
                Toast.makeText(
                    context,
                    error.message ?: "Failed to fetch free slots",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    } catch (e: Exception) {
        Toast.makeText(context, "Exception occurred: ${e.message}", Toast.LENGTH_SHORT).show()
    }
}

fun handleGetCurrentBookingOfUser(
    context: Context,
    userId: Int,
    parkingAreaId: Int,
    navController: NavController,
    repository: BookingRepository
) {
    repository.getCurrentBookingOfUserRepository(userId, parkingAreaId) { result ->
        result.onSuccess { bookedSlots ->
            val json = Uri.encode(Gson().toJson(bookedSlots))
            navController.navigate(Routes.myBookings + "/$json/$parkingAreaId")

            if (bookedSlots.isEmpty()) {
                Toast.makeText(context, "No booked slots", Toast.LENGTH_SHORT).show()
            }
        }
        result.onFailure { error ->
            val json = Uri.encode(Gson().toJson(emptyList<BookingData>()))
            navController.navigate(Routes.myBookings + "/$json/$parkingAreaId")
            Toast.makeText(
                context,
                error.message ?: "Failed to find data",
                Toast.LENGTH_SHORT
            ).show()
        }
    }
}

fun handleBookingCheckForToday(
    context: Context,
    userId: Int,
    parkingAreaId: Int,
    navController: NavController,
    repository: BookingRepository
) {
    val today = LocalDate.now().toString()

    repository.getBookingByUserParkingAndDate(userId, parkingAreaId, today) { result ->
        result.onSuccess { response ->
              val slotName = response.data?.firstOrNull()?.slot?.name
            val ticketLine1 = response.data?.firstOrNull()?.parkingArea?.ticketLine1
            val ticketLine2 = response.data?.firstOrNull()?.parkingArea?.ticketLine2
            if (slotName != null) {
                navController.navigate("${Routes.parkingTicket}/$slotName/$ticketLine1/$ticketLine2")
            } else {
                Toast.makeText(context, "No Booking for today", Toast.LENGTH_SHORT).show()
            }
        }

        result.onFailure { error ->
            Toast.makeText(
                context,
                error.message ?: "Failed to fetch booking",
                Toast.LENGTH_SHORT
            ).show()
        }
    }
}

@Composable
fun DashboardButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    icon: ImageVector
) {
    Button(
        onClick = onClick,
        modifier = modifier
            .fillMaxWidth(0.8f)
            .height(60.dp)
            .padding(2.dp),
        shape = RoundedCornerShape(16.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = Color.White,
            contentColor = Color.Black
        ),
        elevation = ButtonDefaults.buttonElevation(defaultElevation = 4.dp)
    ) {
        // CONTENT ROW:
        // Use a Row to arrange the icon and text horizontally
        // Added Modifier.fillMaxWidth() and Arrangement.Start
        Row(
            modifier = Modifier.fillMaxWidth(), // This makes the row take the entire button width
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Start // This aligns content to the start
        ) {
            // Your leading icon
            Icon(
                imageVector = icon,
                contentDescription = null, // decorative icon
                modifier = Modifier.size(24.dp) // adjust size as needed
            )

            // Add some space between the icon and the text
            Spacer(modifier = Modifier.width(12.dp))

            // Your button text
            Text(
                text = text,
                fontSize = 18.sp,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}
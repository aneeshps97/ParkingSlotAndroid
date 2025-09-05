package com.example.parkingslot.mainpages.ParkingArea

import android.content.Context
import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.parkingslot.Route.Routes
import com.example.parkingslot.mainpages.background.PageBackground
import com.example.parkingslot.customresuables.confirm.ConfirmPopUp
import com.example.parkingslot.webConnect.dto.booking.BookingData
import com.example.parkingslot.webConnect.dto.booking.BookingResponse
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
    adminId:String,
) {
    val context = LocalContext.current
    val sharedPref = remember { context.getSharedPreferences("loginPref", Context.MODE_PRIVATE) }
    val userId = sharedPref.getInt("user_id", 1)
    var showConfirmationDialog by remember { mutableStateOf(false) }
    var bookingRepository = BookingRepository()
    var parkingAreaRepository = ParkingAreaRepository()

    PageBackground {
        Box(modifier = Modifier.fillMaxSize()) {
            ConfirmPopUp(
                showDialog = showConfirmationDialog,
                onDismiss = { showConfirmationDialog = false },
                onConfirm = {

                }
            )
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.TopCenter
            ) {
                Text(parkingAreaName)
            }
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    DashboardButton("View Ticket") {
                        val today = LocalDate.now().toString()

                        handleBookingCheckForToday(
                                parkingAreaId = parkingAreaId.toInt(),
                                context = context,
                                userId = userId,
                                navController = navController,
                                repository = bookingRepository
                            )
                    }
                    DashboardButton("Available Slots") {
                        handleGetFreeSlotsInParkingArea(
                            userId = userId,
                            parkingAreaId = Integer.parseInt(parkingAreaId),
                            navController = navController,
                            context = context,
                            repository = parkingAreaRepository
                        )
                    }
                    DashboardButton("My Bookings") {
                        handleGetCurrentBookingOfUser(
                            userId = userId,
                            parkingAreaId = Integer.parseInt(parkingAreaId),
                            navController = navController,
                            context = context,
                            repository = bookingRepository
                        )
                    }
                    if(userId.toString().equals(adminId.toString())){
                        Spacer(modifier.height(30.dp))
                        DashboardButton("Delete ParkingArea") {
                            handleDeleteParkingArea(Integer.parseInt(parkingAreaId),context,navController)
                        }
                        DashboardButton("Update ParkingArea") {
                            navController.navigate(Routes.editParkingArea + "/" + parkingAreaId + "/" + parkingAreaName)
                        }
                    }
                }
            }
        }
    }
}

fun handleDeleteParkingArea(parkingAreaId: Int,context: Context,navController: NavController){
    try {
        //when it is success navigate to home page
        val api = RetrofitService.getRetrofit().create(ParkingSlotApi::class.java)
        api.deleteParkingArea(parkingAreaId)
            .enqueue(object : Callback<ParkingAreaResponse>{
                override fun onResponse(
                    call: Call<ParkingAreaResponse?>,
                    response: Response<ParkingAreaResponse?>
                ) {
                    if(response.body()?.status==0){
                        Toast.makeText(context, "parking area deleted", Toast.LENGTH_SHORT)
                            .show()
                        navController.navigate(Routes.homePage)
                    }else{
                        Toast.makeText(context, "failed", Toast.LENGTH_SHORT)
                            .show()
                    }

                }

                override fun onFailure(
                    call: Call<ParkingAreaResponse?>,
                    t: Throwable
                ) {
                    Toast.makeText(context, "Error: ${t.message}", Toast.LENGTH_SHORT)
                        .show()
                }
            })

    }catch (e: Exception) {
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
            if (slotName != null) {
                navController.navigate("${Routes.parkingTicket}/$slotName")
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
fun DashboardButton(text: String, onClick: () -> Unit = {}) {
    Button(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth(0.8f)
            .height(60.dp)
            .padding(2.dp),
        shape = RoundedCornerShape(16.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = Color.White, contentColor = Color.Black
        ),
        border = BorderStroke(2.dp, Color.Black),
        elevation = null
    ) {
        Text(text = text, fontSize = 18.sp, fontWeight = FontWeight.SemiBold)
    }
}

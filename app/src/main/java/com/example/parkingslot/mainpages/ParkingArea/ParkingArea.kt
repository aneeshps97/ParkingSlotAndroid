package com.example.parkingslot.mainpages.ParkingArea

import android.content.Context
import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Button
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavController
import com.example.parkingslot.Route.Routes
import com.example.parkingslot.mainpages.background.PageBackground
import com.example.parkingslot.customresuables.buttons.LogOutButton
import com.example.parkingslot.customresuables.confirm.ConfirmPopUp
import com.example.parkingslot.sharedView.BookingViewModel
import com.example.parkingslot.webConnect.dto.booking.BookingData
import com.example.parkingslot.webConnect.dto.booking.BookingResponse
import com.example.parkingslot.webConnect.dto.parkingArea.ParkingAreaData
import com.example.parkingslot.webConnect.dto.parkingArea.ParkingAreaResponse
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
    parkingAreaId: String
) {
    val context = LocalContext.current
    val sharedPref = remember { context.getSharedPreferences("loginPref", Context.MODE_PRIVATE) }
    val userId = sharedPref.getInt("user_id", 1)
    var showConfirmationDialog by remember { mutableStateOf(false) }

    PageBackground {
        ConfirmPopUp(
            showDialog = showConfirmationDialog,
            onDismiss = { showConfirmationDialog = false },
            onConfirm = {
                val sharedPref = context.getSharedPreferences("loginPref", Context.MODE_PRIVATE)
                with(sharedPref.edit()) {
                    clear()   // or remove("your_key") for specific key
                    apply()
                }
                navController.navigate(Routes.login)
                // Handle confirm logic here
            }
        )
        Box(contentAlignment = Alignment.Center) {
            LogOutButton({
                showConfirmationDialog = true
            })


            Column(
                verticalArrangement = Arrangement.spacedBy(12.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                DashboardButton("View Ticket") {
                    val today = LocalDate.now().toString()
                    val currentBookingData: BookingResponse? = getBookingByUserParkingAndDate(
                        parkingAreaId = parkingAreaId.toInt(),
                        context = context,
                        date = today,
                        userId = userId,
                        navController = navController
                    )

                }
                DashboardButton("Available Slots") {
                    getFreeSlotsInParkingArea(
                        userId = userId,
                        parkingAreaId = Integer.parseInt(parkingAreaId),
                        navController = navController,
                        context = context
                    )
                }
                DashboardButton("My Bookings") {
                    getCurrentBookingOfUser(
                        userId = userId,
                        parkingAreaId = Integer.parseInt(parkingAreaId),
                        navController = navController,
                        context = context
                    )

                }

            }
        }

    }
}

fun getFreeSlotsInParkingArea(
    userId: Int,
    parkingAreaId: Int,
    navController: NavController,
    context: Context
) {
    try {
        val bookedSlots: MutableList<BookingData> = mutableListOf()
        val api = RetrofitService.getRetrofit().create(ParkingSlotApi::class.java)
        api.getFreeSlotsInParkingArea(parkingAreaId)
            .enqueue(object : Callback<BookingResponse> {
                override fun onResponse(
                    call: Call<BookingResponse>,
                    response: Response<BookingResponse>
                ) {
                    if (response.isSuccessful && response.body() != null) {
                        if(response.body()?.status==0){
                            response.body()?.data?.let { bookedSlots.addAll(it) }
                            val json = Uri.encode(Gson().toJson(bookedSlots))
                            navController.navigate(Routes.availableSlots + "/$json/" + parkingAreaId)
                        }else{
                            Toast.makeText(context, "No free slots available", Toast.LENGTH_SHORT).show()
                        }
                    } else {
                        Toast.makeText(context, "Failed to find data", Toast.LENGTH_SHORT).show()
                        Log.e("BookingError", "Response unsuccessful or body is null")
                    }
                }

                override fun onFailure(call: Call<BookingResponse>, t: Throwable) {
                    Toast.makeText(context, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
                    Log.e("BookingError", "API call failed", t)
                }
            })
    } catch (e: Exception) {
        Toast.makeText(context, "Exception occurred: ${e.message}", Toast.LENGTH_SHORT).show()
        Log.e("BookingException", "Exception in getCurrentBookingOfUser", e)
    }
}


fun getCurrentBookingOfUser(
    userId: Int,
    parkingAreaId: Int,
    navController: NavController,
    context: Context
) {
    try {
        val bookedSlots: MutableList<BookingData> = mutableListOf()
        val api = RetrofitService.getRetrofit().create(ParkingSlotApi::class.java)

        api.getBookingByUserForParkingArea(userId, parkingAreaId)
            .enqueue(object : Callback<BookingResponse> {
                override fun onResponse(
                    call: Call<BookingResponse>,
                    response: Response<BookingResponse>
                ) {
                    if (response.isSuccessful && response.body() != null) {
                        if(response.body()?.status==0){
                            response.body()?.data?.let { bookedSlots.addAll(it) }
                            val json = Uri.encode(Gson().toJson(bookedSlots))
                            navController.navigate(Routes.myBookings + "/$json/" + parkingAreaId)
                        }else{
                            Toast.makeText(context, "No booked slots", Toast.LENGTH_SHORT).show()
                        }
                    } else {
                        Toast.makeText(context, "Failed to find data", Toast.LENGTH_SHORT).show()
                        Log.e("BookingError", "Response unsuccessful or body is null")
                    }
                }

                override fun onFailure(call: Call<BookingResponse>, t: Throwable) {
                    Toast.makeText(context, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
                    Log.e("BookingError", "API call failed", t)
                }
            })
    } catch (e: Exception) {
        Toast.makeText(context, "Exception occurred: ${e.message}", Toast.LENGTH_SHORT).show()
        Log.e("BookingException", "Exception in getCurrentBookingOfUser", e)
    }
}


fun getBookingByUserParkingAndDate(
    parkingAreaId: Int,
    context: Context,
    date: String,
    userId: Int,
    navController: NavController
): BookingResponse? {
    val bookingResponse: BookingResponse? = null
    val api = RetrofitService.getRetrofit().create(ParkingSlotApi::class.java)
    val today = LocalDate.now().toString()
    api.getBookingByUserParkingAndDate(userId, Integer.valueOf(parkingAreaId), date = today)
        .enqueue(object : Callback<BookingResponse> {
            override fun onResponse(
                call: Call<BookingResponse>,
                response: Response<BookingResponse>
            ) {
                if (response.body() != null) {
                    val slotName = response.body()?.data?.get(0)?.slot?.name
                    navController.navigate(Routes.parkingTicket + "/" + slotName)
                } else {
                    Toast.makeText(context, "unable to fetch data", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<BookingResponse>, t: Throwable) {
                Toast.makeText(context, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })

    return bookingResponse;
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



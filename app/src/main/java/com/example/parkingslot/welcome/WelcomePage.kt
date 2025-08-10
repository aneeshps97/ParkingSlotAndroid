package com.example.parkingslot.welcome

import android.content.Context
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.BackHandler
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
import com.example.parkingslot.background.PageBackground
import com.example.parkingslot.buttons.LogOutButton
import com.example.parkingslot.confirm.ConfirmPopUp
import com.example.parkingslot.sharedView.BookingViewModel
import com.example.parkingslot.webConnect.requestresponse.BookingResponse
import com.example.parkingslot.webConnect.retrofit.ParkingSlotApi
import com.example.parkingslot.webConnect.retrofit.RetrofitService
import com.google.gson.Gson
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.time.LocalDate

@Composable
fun welcomePage(navController: NavController, modifier: Modifier = Modifier,bookingViewModel: BookingViewModel) {
    BackHandler {
        // Do nothing or show a dialog instead
    }
    val context = LocalContext.current
    val sharedPref = remember { context.getSharedPreferences("loginPref", Context.MODE_PRIVATE) }
    val userId = sharedPref.getInt("user_id",1)
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


                    val api = RetrofitService.getRetrofit().create(ParkingSlotApi::class.java)
                    val today = LocalDate.now().toString()
                    api.findCurrentParkingSlot(userId,today).enqueue(object : Callback<BookingResponse> {

                        override fun onResponse(call: Call<BookingResponse>, response: Response<BookingResponse>) {
                            if ( response.body()!=null) {
                                val slotNumber = response.body()?.slotNo
                                navController.navigate(Routes.parkingTicket + "/"+slotNumber)
                            } else {
                                //Toast.makeText(context, "Login failed", Toast.LENGTH_SHORT).show()
                            }
                        }

                        override fun onFailure(call: Call<BookingResponse>, t: Throwable) {
                            //Toast.makeText(context, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
                        }
                    })


                }
                DashboardButton("Available Slots") {


                    var freeSlots: MutableList<BookingResponse> = mutableListOf()
                    val api = RetrofitService.getRetrofit().create(ParkingSlotApi::class.java)
                    api.findFreeSlots().enqueue(object : Callback<List<BookingResponse>> {
                        override fun onResponse(
                            call: Call<List<BookingResponse>>,
                            response: Response<List<BookingResponse>>
                        ) {
                            if ( response.body()!=null) {
                                freeSlots.addAll(response.body()!!)
                                bookingViewModel.slotsData = response.body()!!
                                val json = Uri.encode(Gson().toJson(freeSlots))
                                navController.navigate(Routes.availableSlots+ "/$json")
                            } else {
                                //Toast.makeText(context, "Login failed", Toast.LENGTH_SHORT).show()
                            }
                        }

                        override fun onFailure(call: Call<List<BookingResponse>>, t: Throwable) {
                            Toast.makeText(context, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
                        }
                    })

                }
                DashboardButton("My Bookings") {
                        var bookedSlots: MutableList<BookingResponse> = mutableListOf()
                        val api = RetrofitService.getRetrofit().create(ParkingSlotApi::class.java)
                        api.findSlotsByUserId(userId).enqueue(object : Callback<List<BookingResponse>> {
                            override fun onResponse(
                                call: Call<List<BookingResponse>>,
                                response: Response<List<BookingResponse>>
                            ) {
                                if ( response.body()!=null) {
                                    bookedSlots.addAll(response.body()!!)
                                    bookingViewModel.slotsData = response.body()!!
                                    val json = Uri.encode(Gson().toJson(bookedSlots))
                                     navController.navigate(Routes.myBookings+ "/$json")
                                } else {
                                    //Toast.makeText(context, "Login failed", Toast.LENGTH_SHORT).show()
                                }
                            }

                            override fun onFailure(call: Call<List<BookingResponse>>, t: Throwable) {
                                Toast.makeText(context, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
                            }
                        })

                }

            }
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



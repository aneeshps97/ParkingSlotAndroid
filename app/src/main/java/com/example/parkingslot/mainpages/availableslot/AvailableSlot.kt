package com.example.parkingslot.mainpages.availableslot

import android.content.Context
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavController
import com.example.parkingslot.Route.Routes
import com.example.parkingslot.mainpages.background.PageBackground
import com.example.parkingslot.customresuables.calender.Calender
import com.example.parkingslot.customresuables.confirm.ConfirmPopUp
import com.example.parkingslot.mainpages.ParkingArea.getFreeSlotsInParkingArea
import com.example.parkingslot.webConnect.dto.booking.BookingData
import com.example.parkingslot.webConnect.dto.booking.BookingResponse
import com.example.parkingslot.webConnect.retrofit.ParkingSlotApi
import com.example.parkingslot.webConnect.retrofit.RetrofitService
import com.google.gson.Gson
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@Composable
fun AvailableSlot(
    modifier: Modifier = Modifier,
    year: Int,
    month: Int,
    navController: NavController,
    bookingData: List<BookingData> = emptyList<BookingData>(),
    parkingAreaId:String?
) {
    var showConfirmationDialog by remember { mutableStateOf(false) }
    val context = LocalContext.current
    val today = LocalDate.now()
    val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
    val formattedDate = today.format(formatter)
    var dateSelected:String=formattedDate
    val sharedPref = remember { context.getSharedPreferences("loginPref", Context.MODE_PRIVATE) }
    val userId = sharedPref.getInt("user_id",1)
    PageBackground() {
        BackHandler {
            navController.navigate(Routes.homePage)
        }
        ConfirmPopUp(
            showDialog = showConfirmationDialog,
            onDismiss = { showConfirmationDialog = false },
            onConfirm = {
                val matchedId = bookingData.find {it.date == dateSelected }?.bookingId
                 val api = RetrofitService.getRetrofit().create(ParkingSlotApi::class.java)
                api.bookSlotForUser(userId=userId,bookingId = matchedId as Int?).enqueue(object : Callback<BookingResponse> {
                    override fun onResponse(call: Call<BookingResponse>, response: Response<BookingResponse>) {
                        if ( response.body()!=null) {
                            if(response.body()?.status==0){
                                Toast.makeText(context, "Slot Booked", Toast.LENGTH_SHORT).show()
                                getFreeSlotsInParkingArea(
                                    userId = userId,
                                    parkingAreaId = Integer.parseInt(parkingAreaId),
                                    navController = navController,
                                    context = context
                                )
                            }else{
                                Toast.makeText(context, "Failed", Toast.LENGTH_SHORT).show()
                            }
                        } else {
                            Toast.makeText(context, "Failed", Toast.LENGTH_SHORT).show()
                        }
                    }

                    override fun onFailure(call: Call<BookingResponse>, t: Throwable) {
                        Toast.makeText(context, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
                    }
                 }

                 )
                showConfirmationDialog = false
            }
        )
        Box(contentAlignment = Alignment.Center) {
            Calender(
                modifier =modifier,
                year =year,
                month = month,
                navController = navController,
                onClick = { selectedDate ->
                    dateSelected = selectedDate
                    showConfirmationDialog = true
                },
                calenderRef = Routes.availableSlots,
                bookingData = bookingData,
                parkingAreaId = parkingAreaId.toString()
            )
        }
    }
}


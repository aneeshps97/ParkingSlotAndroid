package com.example.parkingslot.mainpages.MyBookings

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.layout.Box
import androidx.compose.material3.Text
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
import com.example.parkingslot.customresuables.popUp.TransferOrReleasePopup
import com.example.parkingslot.webConnect.dto.booking.BookingResponse
import com.example.parkingslot.webConnect.retrofit.ParkingSlotApi
import com.example.parkingslot.webConnect.retrofit.RetrofitService
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

@Composable
fun myBookings(
    modifier: Modifier = Modifier,
    year: Int = 2025,
    month: Int = 8,
    navController: NavController,
    bookingData: List<BookingResponse>
) {
    var showDialog by remember { mutableStateOf(false) }
    var showConfirmationDialog by remember { mutableStateOf(false) }
    val context = LocalContext.current
    var dateSelected:String="2025-08-07"
    val sharedPref = remember { context.getSharedPreferences("loginPref", Context.MODE_PRIVATE) }
    val userId = sharedPref.getInt("user_id",1)
    PageBackground() {
        TransferOrReleasePopup(
            showDialog = showDialog,
            onDismiss = { showDialog = false },
            onTransfer = {
                showDialog = false
                navController.navigate(Routes.transferSlot)
            },
            onRelease = {
                showDialog = false
                showConfirmationDialog = true
                {showConfirmationDialog = true}
            },
            navController = navController
        )

        ConfirmPopUp(
            showDialog = showConfirmationDialog,
            onDismiss = { showConfirmationDialog = false },
            onConfirm = {


                val matchedId = bookingData.find { it.userId == userId && it.date == dateSelected }?.id
                val api = RetrofitService.getRetrofit().create(ParkingSlotApi::class.java)
                api.releaseSlot(slotId = matchedId).enqueue(object : Callback<BookingResponse> {
                    override fun onResponse(call: Call<BookingResponse>, response: Response<BookingResponse>) {
                        if ( response.body()!=null) {
                            Toast.makeText(context, "slot released", Toast.LENGTH_SHORT).show()
                        } else {
                            //Toast.makeText(context, "Login failed", Toast.LENGTH_SHORT).show()
                        }
                    }

                    override fun onFailure(call: Call<BookingResponse>, t: Throwable) {
                        //Toast.makeText(context, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
                    }
                })

                showDialog = false
            }
        )

        Text("My Bookings")
        Box(contentAlignment = Alignment.Center) {
            val context = LocalContext.current
           /* BackButton({
                navController.navigate(Routes.welcomePage)
            })*/

            Calender(
                modifier=modifier,
                year=year,
                month = month,
                navController = navController,
                onClick = {selectedDate ->
                    dateSelected=selectedDate
                    showDialog = true },
                calenderRef = Routes.myBookings,
                bookingData = bookingData
            )

        }
    }
}


package com.example.parkingslot.availableSloats

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
import com.example.parkingslot.background.PageBackground
import com.example.parkingslot.buttons.BackButton
import com.example.parkingslot.calender.Calender
import com.example.parkingslot.confirm.ConfirmPopUp
import com.example.parkingslot.webConnect.requestresponse.BookingResponse
import com.example.parkingslot.webConnect.retrofit.ParkingSlotApi
import com.example.parkingslot.webConnect.retrofit.RetrofitService
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

@Composable
fun AvailableSlot(
    modifier: Modifier = Modifier,
    year: Int = 2025,
    month: Int = 8,
    navController: NavController,
    bookingData: List<BookingResponse> = emptyList<BookingResponse>(),
    pid:Int=0
) {
    var showConfirmationDialog by remember { mutableStateOf(false) }
    val context = LocalContext.current
    var dateSelected:String="2025-08-07"
    val sharedPref = remember { context.getSharedPreferences("loginPref", Context.MODE_PRIVATE) }
    val userId = sharedPref.getInt("user_id",1)
    PageBackground() {

        ConfirmPopUp(
            showDialog = showConfirmationDialog,
            onDismiss = { showConfirmationDialog = false },
            onConfirm = {


                val matchedId = bookingData.find {it.date == dateSelected }?.id
                Toast.makeText(context, "bookingData ::"+bookingData, Toast.LENGTH_SHORT).show()
                Toast.makeText(context, "dateSelected ::"+dateSelected, Toast.LENGTH_SHORT).show()
                Toast.makeText(context, "matched slot id ::"+matchedId, Toast.LENGTH_SHORT).show()
                 val api = RetrofitService.getRetrofit().create(ParkingSlotApi::class.java)
                 api.bookFreeSlot(slotId = matchedId,userId=userId).enqueue(object : Callback<BookingResponse> {
                     override fun onResponse(call: Call<BookingResponse>, response: Response<BookingResponse>) {
                         if ( response.body()!=null) {
                             Toast.makeText(context, "slot booked", Toast.LENGTH_SHORT).show()
                         } else {
                             //Toast.makeText(context, "Login failed", Toast.LENGTH_SHORT).show()
                         }
                     }

                     override fun onFailure(call: Call<BookingResponse>, t: Throwable) {
                         //Toast.makeText(context, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
                     }
                 })

                showConfirmationDialog = false
            }
        )

        Text("Available slots")
        Box(contentAlignment = Alignment.Center) {
           /* BackButton({
                navController.navigate(Routes.welcomePage+"/"+pid)
            })*/
            Calender(
                modifier=modifier,
                year=year,
                month = month,
                navController = navController,
                onClick = { selectedDate ->
                    dateSelected = selectedDate
                    showConfirmationDialog = true
                },
                calenderRef = Routes.availableSlots,
                bookingData = bookingData
            )
        }
    }
}
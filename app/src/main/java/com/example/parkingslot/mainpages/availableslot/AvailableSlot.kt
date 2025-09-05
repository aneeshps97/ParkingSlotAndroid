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
import com.example.parkingslot.mainpages.ParkingArea.handleGetFreeSlotsInParkingArea
import com.example.parkingslot.webConnect.dto.booking.BookingData
import com.example.parkingslot.webConnect.dto.booking.BookingResponse
import com.example.parkingslot.webConnect.repository.ParkingAreaRepository
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
    var parkingAreaRepository = ParkingAreaRepository()
    PageBackground() {
        BackHandler {
            navController.navigate(Routes.homePage)
        }
        ConfirmPopUp(
            showDialog = showConfirmationDialog,
            onDismiss = { showConfirmationDialog = false },
            onConfirm = {
                handleSlotBooking(
                    bookingData = bookingData,
                    dateSelected = dateSelected,
                    userId = userId,
                    parkingAreaId = parkingAreaId.toString(),
                    navController = navController,
                    context = context,
                    onBookingCompleted = {showConfirmationDialog = false},
                    repository = parkingAreaRepository
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

fun handleSlotBooking(
    bookingData: List<BookingData>,
    dateSelected: String,
    userId: Int,
    parkingAreaId: String,
    navController: NavController,
    context: Context,
    onBookingCompleted: () -> Unit,
    repository: ParkingAreaRepository
) {
    val matchedId = bookingData.find { it.date == dateSelected }?.bookingId

    if (matchedId == null) {
        Toast.makeText(context, "No booking available for selected date", Toast.LENGTH_SHORT).show()
        return
    }

    repository.bookSlotForUser(userId, matchedId) { result ->
        result.onSuccess {
            Toast.makeText(context, "Slot Booked", Toast.LENGTH_SHORT).show()
            handleGetFreeSlotsInParkingArea(
                userId = userId,
                parkingAreaId = Integer.parseInt(parkingAreaId),
                navController = navController,
                context = context,
                repository = repository
            )
            onBookingCompleted() // e.g. close confirmation dialog
        }
        result.onFailure { error ->
            Toast.makeText(
                context,
                error.message ?: "Failed to book slot",
                Toast.LENGTH_SHORT
            ).show()
        }
    }
}

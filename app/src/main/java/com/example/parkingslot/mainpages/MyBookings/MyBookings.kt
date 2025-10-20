package com.example.parkingslot.mainpages.MyBookings

import android.content.Context
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.parkingslot.Route.Routes
import com.example.parkingslot.mainpages.background.PageBackground
import com.example.parkingslot.customresuables.calender.Calender
import com.example.parkingslot.customresuables.confirm.ConfirmPopUp
import com.example.parkingslot.customresuables.popUp.TransferOrReleasePopup
import com.example.parkingslot.customresuables.popUp.TransferUserListPopUp
import com.example.parkingslot.mainpages.ParkingArea.handleGetCurrentBookingOfUser
import com.example.parkingslot.webConnect.dto.booking.BookingData
import com.example.parkingslot.webConnect.dto.booking.BookingResponse
import com.example.parkingslot.webConnect.dto.parkingArea.ParkingAreaResponse
import com.example.parkingslot.webConnect.dto.user.User
import com.example.parkingslot.webConnect.repository.BookingRepository
import com.example.parkingslot.webConnect.repository.ParkingAreaRepository
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
    bookingData: List<BookingData>,
    parkingAreaId: String?
) {
    var showDialog by remember { mutableStateOf(false) }
    var showTransferDialog by remember { mutableStateOf(value = false) }
    var showConfirmationDialog by remember { mutableStateOf(false) }
    var showConfirmationDialogForTransferingSlot by remember { mutableStateOf(false) }
    val context = LocalContext.current
    var dateSelected:String="2025-08-07"
    val sharedPref = remember { context.getSharedPreferences("loginPref", Context.MODE_PRIVATE) }
    val userId = sharedPref.getInt("user_id",1)
    var userList: MutableList<User> = mutableListOf()
    var selectedUserIdForTransfer: Int=0
    var parkingAreaRepository: ParkingAreaRepository = ParkingAreaRepository()
    var bookingRespository = BookingRepository()
    PageBackground() {
        BackHandler {
            navController.navigate(Routes.viewYourParkingAreas)
        }
        TransferOrReleasePopup(
            showDialog = showDialog,
            onDismiss = { showDialog = false },
            onTransfer = {
                showDialog = false
                buildUserData(parkingAreaId.toString(),userList,context,parkingAreaRepository,{showTransferDialog = true})
            },
            onRelease = {
                showDialog = false
                showConfirmationDialog = true
                {showConfirmationDialog = true}
            },
            navController = navController,
            slotName = bookingData.find { it.date == dateSelected }?.slot?.name
        )
        TransferUserListPopUp(
            showDialog = showTransferDialog,
            onDismiss = { showTransferDialog = false },
            navController = navController,
            slotName = bookingData.find { it.date == dateSelected }?.slot?.name,
            userList = userList,
            onUserSelected = {selectedUserId ->
                selectedUserIdForTransfer=selectedUserId
                showTransferDialog = false
                showConfirmationDialogForTransferingSlot=true
            }
        )

        ConfirmPopUp(
            showDialog = showConfirmationDialogForTransferingSlot,
            onDismiss = { showConfirmationDialogForTransferingSlot = false },
            onConfirm = {

                handleSlotTransfer(
                    bookingData = bookingData,
                    dateSelected = dateSelected,
                    selectedUserIdForTransfer = selectedUserIdForTransfer,
                    context = context,
                    onTransferCompleted = {
                        showConfirmationDialogForTransferingSlot = false
                        handleGetCurrentBookingOfUser(
                            userId = userId,
                            parkingAreaId = parkingAreaId?.toInt() ?: 0,
                            navController = navController,
                            context = context,
                            repository = BookingRepository()
                        )
                    },
                    repository = parkingAreaRepository
                )

                showConfirmationDialogForTransferingSlot = false
            },
            "TransferSlot",
            "Are you sure?"
        )

        ConfirmPopUp(
            showDialog = showConfirmationDialog,
            onDismiss = { showConfirmationDialog = false },
            onConfirm = {
                handleReleaseSlot(
                    bookingData = bookingData,
                    dateSelected = dateSelected,
                    userId = userId,
                    parkingAreaId = parkingAreaId.toString(),
                    navController = navController,
                    context = context,
                    repository = parkingAreaRepository
                )

                showDialog = false
                showConfirmationDialog = false
            },
            "Release slot",
            "Are you sure?"
        )
        Box(contentAlignment = Alignment.Center,modifier=modifier.padding(10.dp).shadow(
            elevation = 8.dp, // Adjust this value to change the shadow depth
            shape = RoundedCornerShape(16.dp)
        )
            .background(Color.White, shape = RoundedCornerShape(16.dp))) {
            val context = LocalContext.current
            Calender(
                modifier =modifier,
                year =year,
                month = month,
                navController = navController,
                onClick = { selectedDate ->
                    dateSelected=selectedDate
                    showDialog = true },
                calenderRef = Routes.myBookings,
                bookingData = bookingData,
                parkingAreaId = parkingAreaId.toString()
            )

        }
    }
}


fun handleSlotTransfer(
    bookingData: List<BookingData>,
    dateSelected: String,
    selectedUserIdForTransfer: Int,
    context: Context,
    onTransferCompleted: () -> Unit,
    repository: ParkingAreaRepository
) {
    val matchedId = bookingData.find { it.date == dateSelected }?.bookingId

    if (matchedId == null) {
        Toast.makeText(context, "No booking found for selected date", Toast.LENGTH_SHORT).show()
        return
    }

    repository.bookSlotForUser(selectedUserIdForTransfer, matchedId) { result ->
        result.onSuccess {
            Toast.makeText(context, "Slot reallocated", Toast.LENGTH_SHORT).show()
            onTransferCompleted()
        }
        result.onFailure { error ->
            Toast.makeText(
                context,
                error.message ?: "Failed to reallocate slot",
                Toast.LENGTH_SHORT
            ).show()
        }
    }
}


fun handleReleaseSlot(
    bookingData: List<BookingData>,
    dateSelected: String,
    userId: Int,
    parkingAreaId: String,
    navController: NavController,
    context: Context,
    repository: ParkingAreaRepository
) {
    val matchedId = bookingData.find { it.date == dateSelected }?.bookingId

    if (matchedId == null) {
        Toast.makeText(context, "No booking found for selected date", Toast.LENGTH_SHORT).show()
        return
    }

    repository.releaseSlotRepository(matchedId) { result ->
        result.onSuccess {
            Toast.makeText(context, "Slot released", Toast.LENGTH_SHORT).show()
            // Refresh booking data
            handleGetCurrentBookingOfUser(
                userId = userId,
                parkingAreaId = parkingAreaId.toInt(),
                navController = navController,
                context = context,
                repository = BookingRepository()
            )
        }
        result.onFailure { error ->
            Toast.makeText(
                context,
                error.message ?: "Failed to release slot",
                Toast.LENGTH_SHORT
            ).show()
        }
    }
}

fun buildUserData(
    parkingAreaId: String,
    userList: MutableList<User>,
    context: Context,
    repository: ParkingAreaRepository,
    onTransferReady: () -> Unit
) {
    repository.findParkingAreaById(parkingAreaId) { result ->
        result.onSuccess { response ->
            userList.clear()

            val usersFromApi: List<User> = response.data?.users?.map { userData ->
                User(
                    id = userData.userId,
                    name = userData.name
                )
            } ?: emptyList()

            userList.addAll(usersFromApi)
            onTransferReady()
        }

        result.onFailure { error ->
            Toast.makeText(
                context,
                error.message ?: "Failed to fetch users",
                Toast.LENGTH_SHORT
            ).show()
        }
    }
}




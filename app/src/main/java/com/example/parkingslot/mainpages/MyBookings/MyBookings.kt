package com.example.parkingslot.mainpages.MyBookings

import android.content.Context
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
import com.example.parkingslot.customresuables.popUp.TransferOrReleasePopup
import com.example.parkingslot.customresuables.popUp.TransferUserListPopUp
import com.example.parkingslot.mainpages.ParkingArea.getCurrentBookingOfUser
import com.example.parkingslot.webConnect.dto.booking.BookingData
import com.example.parkingslot.webConnect.dto.booking.BookingResponse
import com.example.parkingslot.webConnect.dto.parkingArea.ParkingAreaResponse
import com.example.parkingslot.webConnect.dto.user.User
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
    PageBackground() {
        BackHandler {
            navController.navigate(Routes.parkingArea + "/$parkingAreaId")
        }
        TransferOrReleasePopup(
            showDialog = showDialog,
            onDismiss = { showDialog = false },
            onTransfer = {
                showDialog = false
                buildUserData(parkingAreaId,userList,context,{showTransferDialog = true})
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
                showConfirmationDialogForTransferingSlot=true
            }
        )

        ConfirmPopUp(
            showDialog = showConfirmationDialogForTransferingSlot,
            onDismiss = { showConfirmationDialogForTransferingSlot = false },
            onConfirm = {
                val matchedId = bookingData.find { it.date == dateSelected }?.bookingId
                val api = RetrofitService.getRetrofit().create(ParkingSlotApi::class.java)
                api.bookSlotForUser(userId = selectedUserIdForTransfer, bookingId = matchedId as Int?)
                    .enqueue(object : Callback<BookingResponse> {
                        override fun onResponse(
                            call: Call<BookingResponse>, response: Response<BookingResponse>
                        ) {
                            if (response.body() != null) {
                                if (response.body()?.status == 0) {
                                    Toast.makeText(context, "Slot reallocated", Toast.LENGTH_SHORT)
                                        .show()
                                    showTransferDialog = false
                                } else {
                                    Toast.makeText(context, "Failed", Toast.LENGTH_SHORT).show()
                                }
                            } else {
                                Toast.makeText(context, "Failed", Toast.LENGTH_SHORT).show()
                            }
                        }

                        override fun onFailure(call: Call<BookingResponse>, t: Throwable) {
                            //Toast.makeText(context, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
                        }
                    })
                showConfirmationDialogForTransferingSlot = false
            },
            "TransferSlot",
            "Are you sure?"
        )

        ConfirmPopUp(
            showDialog = showConfirmationDialog,
            onDismiss = { showConfirmationDialog = false },
            onConfirm = {
                val matchedId = bookingData.find { it.date == dateSelected }?.bookingId
                val api = RetrofitService.getRetrofit().create(ParkingSlotApi::class.java)
                api.releaseSlot(bookingId = matchedId).enqueue(object : Callback<BookingResponse> {
                    override fun onResponse(call: Call<BookingResponse>, response: Response<BookingResponse>) {
                        if ( response.body()!=null) {
                            Toast.makeText(context, "slot released", Toast.LENGTH_SHORT).show()
                            //re render everything this data is fetched from parkingArea we can move it to common if we want
                            getCurrentBookingOfUser(
                                userId = userId,
                                parkingAreaId = Integer.parseInt(parkingAreaId),
                                navController = navController,
                                context = context
                            )
                        } else {
                            Toast.makeText(context, "Failed", Toast.LENGTH_SHORT).show()
                        }
                    }

                    override fun onFailure(call: Call<BookingResponse>, t: Throwable) {
                        Toast.makeText(context, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
                    }
                })
                showDialog = false
                showConfirmationDialog = false
            },
            "Release slot",
            "Are you sure?"
        )
        Box(contentAlignment = Alignment.Center) {
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


fun buildUserData(parkingAreaId: String?, userList: MutableList<User>,context:Context,onTransferReady: () -> Unit) {
    val api = RetrofitService.getRetrofit().create(ParkingSlotApi::class.java)
    api.findParkingAreaById(parkingAreaId = Integer.parseInt(parkingAreaId)).enqueue(object : Callback<ParkingAreaResponse> {
        override fun onResponse(call: Call<ParkingAreaResponse>, response: Response<ParkingAreaResponse>) {
            if ( response.body()!=null) {
                if(response.body()?.status==0){
                    userList.clear()
                    var userDataList = response.body()?.data?.users;
                    val userListFromUserData: List<User> = userDataList?.map { userData ->
                        User(
                            id = userData.userId,
                            name = userData.name,
                        )
                    } ?: mutableListOf()
                    userList.addAll(userListFromUserData);
                    onTransferReady()
                }else{
                    Toast.makeText(context, "Users not found", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(context, "Users not found", Toast.LENGTH_SHORT).show()
            }
        }

        override fun onFailure(call: Call<ParkingAreaResponse>, t: Throwable) {
            Toast.makeText(context, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
        }
    })
}



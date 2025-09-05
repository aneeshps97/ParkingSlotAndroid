package com.example.parkingslot.mainpages.ParkingArea

import android.content.Context
import android.net.Uri
import android.widget.Toast
import androidx.annotation.IntegerRes
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.parkingslot.Route.Routes
import com.example.parkingslot.customresuables.confirm.ConfirmPopUp
import com.example.parkingslot.customresuables.labels.LabelWithTrailingIcon
import com.example.parkingslot.customresuables.popUp.AddParkingSlotPopUp
import com.example.parkingslot.customresuables.popUp.UpdateSlotDataPopUp
import com.example.parkingslot.mainpages.background.PageBackground
import com.example.parkingslot.webConnect.dto.booking.BookingData
import com.example.parkingslot.webConnect.dto.booking.BookingResponse
import com.example.parkingslot.webConnect.dto.parkingArea.ParkingAreaData
import com.example.parkingslot.webConnect.dto.parkingArea.ParkingAreaResponse
import com.example.parkingslot.webConnect.dto.slot.Slot
import com.example.parkingslot.webConnect.dto.slot.SlotData
import com.example.parkingslot.webConnect.dto.slot.SlotDataResponse
import com.example.parkingslot.webConnect.dto.user.User
import com.example.parkingslot.webConnect.dto.user.UserData
import com.example.parkingslot.webConnect.repository.ParkingAreaRepository
import com.example.parkingslot.webConnect.retrofit.ParkingSlotApi
import com.example.parkingslot.webConnect.retrofit.RetrofitService
import com.google.gson.Gson
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

@Composable
fun EditBooking(
    navController: NavController,
    modifier: Modifier = Modifier,
    parkingAreaId: Int,
    bookingDataList: List<BookingData>
) {
    val listOfBookings = remember { mutableStateListOf(*bookingDataList.toTypedArray()) }


    val showRemoveBooking = remember { mutableStateOf(false) }

    val bookingToRemove = remember { mutableStateOf(BookingData.EMPTY) }
    val slotToUpdate = remember { mutableStateOf(SlotData(0, "")) }
    val context: Context = LocalContext.current
    val indexToRemove = remember { mutableStateOf(-1) }
    val indexToUpdate = remember { mutableStateOf(-1) }
    var parkingAreaRepository: ParkingAreaRepository = ParkingAreaRepository();
    ConfirmPopUp(
        showDialog = showRemoveBooking.value,
        onDismiss = { showRemoveBooking.value = false },
        onConfirm = {
            showRemoveBooking.value = false;
            if (indexToRemove.value in listOfBookings.indices) {
                removeBooking(context, listOfBookings, indexToRemove.value)
            }
        },
        "Going to remove",
        "Are you sure?"
    )

    PageBackground {

        Column(modifier = modifier.fillMaxSize()) {
            Box(Modifier.fillMaxSize()) {
                Text(
                    "EDIT",
                    modifier = Modifier
                        .align(Alignment.TopCenter)
                        .padding(top = 16.dp)
                )

                Box(
                    modifier = Modifier
                        .align(Alignment.Center)
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {

                    ScrollableBoxContentForEditBookings(
                        listOfBookings, onRemove = { index ->
                            showRemoveBooking.value = true
                            bookingToRemove.value = listOfBookings.get(index)
                            indexToRemove.value = index
                        },
                        context = context
                    )
                }

                Box(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(bottom = 32.dp)
                ) {
                    Column {

                        Button(
                            onClick = {
                                handleAssignSlotForUsers(context, parkingAreaId.toString(), navController,parkingAreaRepository)
                            },
                            modifier = Modifier
                                .height(60.dp)
                                .fillMaxWidth(0.9f),
                            shape = RoundedCornerShape(16.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color.White,
                                contentColor = Color.Black
                            ),
                            border = BorderStroke(2.dp, Color.Black),
                            elevation = null
                        ) {
                            Text(
                                text = "Create Booking",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }

                        Spacer(modifier.height(20.dp))

                        Button(
                            onClick = {
                                parkingAreaRepository.findParkingAreaById(parkingAreaId.toString()) { result ->
                                    result.onSuccess { response ->
                                        val data = response.data
                                        navController.navigate("${Routes.editParkingArea}/${data.parkingAreaId}/${data.name}")
                                    }
                                    result.onFailure { error ->
                                        Toast.makeText(
                                            context,
                                            error.message ?: "Users not found",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    }
                                }

                            },
                            modifier = Modifier
                                .height(60.dp)
                                .fillMaxWidth(0.9f),
                            shape = RoundedCornerShape(16.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color.White,
                                contentColor = Color.Black
                            ),
                            border = BorderStroke(2.dp, Color.Black),
                            elevation = null
                        ) {
                            Text(
                                text = "Finish",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }

                    }

                }
            }

        }

    }

}


fun removeBooking(context: Context, listOfBooking: SnapshotStateList<BookingData>, index: Int) {
    val api = RetrofitService.getRetrofit().create(ParkingSlotApi::class.java)
    listOfBooking.get(index).bookingId?.let { api.removeBooking(it) }
        ?.enqueue(object : Callback<BookingResponse> {
            override fun onResponse(
                call: Call<BookingResponse?>,
                response: Response<BookingResponse?>
            ) {
                if (response.body()?.status == 0) {
                    Toast.makeText(context, "Slot removed", Toast.LENGTH_SHORT).show()
                    listOfBooking.removeAt(index)
                }
            }

            override fun onFailure(
                call: Call<BookingResponse?>,
                t: Throwable
            ) {
                TODO("Not yet implemented")
            }

        })

}

fun handleAssignSlotForUsers(
    context: Context,
    parkingAreaId: String,
    navController: NavController,
    repository: ParkingAreaRepository
) {
    repository.findParkingAreaById(parkingAreaId) { result ->
        result.onSuccess { response ->
            val json = Uri.encode(Gson().toJson(response.data))
            navController.navigate("${Routes.assignSlotForUsers}/$json")
        }

        result.onFailure { error ->
            Toast.makeText(
                context,
                error.message ?: "Failed to load parking area",
                Toast.LENGTH_SHORT
            ).show()
        }
    }
}


@Composable
fun ScrollableBoxContentForEditBookings(
    listofData: List<BookingData>,
    onRemove: (Int) -> Unit, context: Context
) {
    val scrollState = rememberScrollState()
    Box(
        modifier = Modifier
            .fillMaxHeight(.8f)
            .verticalScroll(scrollState)
            .padding(16.dp)
    ) {
        Column {
            listofData.forEachIndexed { index, data ->
                LabelWithTrailingIcon(
                    "${data.date}\n${data.slot?.name} \n${data.user?.name}",
                    { onRemove(index) }
                )
                Spacer(modifier = Modifier.height(8.dp))
            }
        }
    }
}



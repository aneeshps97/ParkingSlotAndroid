package com.example.parkingslot.mainpages.ParkingArea

import android.content.Context
import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
import com.example.parkingslot.customresuables.textfields.LabeledTextField
import com.example.parkingslot.mainpages.background.PageBackground
import com.example.parkingslot.webConnect.dto.parkingArea.ParkingAreaResponse
import com.example.parkingslot.webConnect.dto.user.User
import com.example.parkingslot.webConnect.retrofit.ParkingSlotApi
import com.example.parkingslot.webConnect.retrofit.RetrofitService
import com.google.gson.Gson
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

@Composable
fun EditParkingArea(
    navController: NavController,
    modifier: Modifier = Modifier,
    parkingAreaId: String?,
    parkingAreaName :String?
) {
    var name by remember { mutableStateOf(parkingAreaName) }
    val context:Context = LocalContext.current
    PageBackground {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            Box(modifier = Modifier.fillMaxSize()) {
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
                    Column {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Box(modifier = Modifier.weight(1f)) {
                                LabeledTextField(
                                    value = name.toString(),
                                    onValueChange = { name = it },
                                    label = "Name"
                                )
                            }

                            Button(
                                onClick = { changeNameOftheParkingArea(name.toString(),parkingAreaId, context) },
                                modifier = Modifier
                                    .height(56.dp) // Match text field height
                                    .align(Alignment.CenterVertically),
                                shape = RoundedCornerShape(8.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = Color.Black,
                                    contentColor = Color.White
                                )
                            ) {
                                Icon(
                                    imageVector = Icons.Default.ArrowForward,
                                    contentDescription = "Edit"
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(20.dp)) // for horizontal spacing

                        Button(
                            onClick = {
                                handleEditSlotsClick(parkingAreaId,context,navController);
                            },
                            modifier = Modifier
                                .height(60.dp)
                                .fillMaxWidth(),
                            shape = RoundedCornerShape(16.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color.White,
                                contentColor = Color.Black
                            ),
                            border = BorderStroke(2.dp, Color.Black),
                            elevation = null
                        ) {
                            Text(
                                text = "Edit Slots",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }

                        Spacer(modifier = Modifier.height(20.dp)) // for horizontal spacing

                        Button(
                            onClick = {},
                            modifier = Modifier
                                .height(60.dp)
                                .fillMaxWidth(),
                            shape = RoundedCornerShape(16.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color.White,
                                contentColor = Color.Black
                            ),
                            border = BorderStroke(2.dp, Color.Black),
                            elevation = null
                        ) {
                            Text(
                                text = "Edit users",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }

                        Spacer(modifier = Modifier.height(20.dp)) // for horizontal spacing

                        Button(
                            onClick = {},
                            modifier = Modifier
                                .height(60.dp)
                                .fillMaxWidth(),
                            shape = RoundedCornerShape(16.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color.White,
                                contentColor = Color.Black
                            ),
                            border = BorderStroke(2.dp, Color.Black),
                            elevation = null
                        ) {
                            Text(
                                text = "Edit Booking",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }

                    }
                }

                Box(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(bottom = 32.dp)
                ) {
                    Button(
                        onClick = {navController.navigate(Routes.homePage)},
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

fun handleEditSlotsClick( parkingAreaId: String?,context: Context,navController: NavController){
    val api = RetrofitService.getRetrofit().create(ParkingSlotApi::class.java)
    api.findParkingAreaById(parkingAreaId = Integer.parseInt(parkingAreaId)).enqueue(object : Callback<ParkingAreaResponse> {
        override fun onResponse(call: Call<ParkingAreaResponse>, response: Response<ParkingAreaResponse>) {
            if ( response.body()!=null) {
                if(response.body()?.status==0){
                    val gson = Gson()
                    val json = Uri.encode(gson.toJson(response.body()?.data))
                    navController.navigate(Routes.editSlots + "/$json")
                }else{
                    Toast.makeText(context, "Data not found ", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(context, "Data not found", Toast.LENGTH_SHORT).show()
            }
        }

        override fun onFailure(call: Call<ParkingAreaResponse>, t: Throwable) {
            Toast.makeText(context, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
        }
    })
}



fun changeNameOftheParkingArea(name:String, parkingAreaId: String?, context:Context){

    // Safely handle the parkingAreaId to prevent crashes from a null or invalid value.
    val id: Int? = parkingAreaId?.toIntOrNull()

    if (id == null) {
        Toast.makeText(context, "Invalid Parking Area ID", Toast.LENGTH_SHORT).show()
        return // Exit the function if the ID is invalid
    }

    val api = RetrofitService.getRetrofit().create(ParkingSlotApi::class.java)

    api.updateParkingAreaName(id = id, newName = name).enqueue(object : Callback<ParkingAreaResponse> {
        override fun onResponse(
            call: Call<ParkingAreaResponse>,
            response: Response<ParkingAreaResponse>
        ) {
            if (response.isSuccessful) {
                val body = response.body()
                if (body != null && body.status == 0) {
                    Toast.makeText(context, "Updated", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(context, "" + body?.message, Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(context, "API Error: ${response.code()}", Toast.LENGTH_SHORT).show()
            }
        }

        override fun onFailure(call: Call<ParkingAreaResponse>, t: Throwable) {
            Toast.makeText(context, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
        }
    })
}




@Preview(showBackground = true)
@Composable
fun EditParkingAreaPreview() {
    // Dummy NavController for preview
    val dummyNavController = rememberNavController()
    EditParkingArea(
        navController = dummyNavController,
        modifier = Modifier.fillMaxSize(),
        parkingAreaId = "2",
        parkingAreaName = "abcde"
    )
}

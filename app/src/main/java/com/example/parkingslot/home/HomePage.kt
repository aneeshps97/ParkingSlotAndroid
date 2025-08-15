package com.example.parkingslot.home

import android.content.Context
import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.parkingslot.Route.Routes
import com.example.parkingslot.background.PageBackground
import com.example.parkingslot.buttons.LogOutButton
import com.example.parkingslot.confirm.ConfirmPopUp
import com.example.parkingslot.sharedView.BookingViewModel
import com.example.parkingslot.webConnect.requestresponse.BookingResponse
import com.example.parkingslot.webConnect.requestresponse.ParkingAreaRequest
import com.example.parkingslot.webConnect.requestresponse.ParkingAreaResponse
import com.example.parkingslot.webConnect.retrofit.ParkingSlotApi
import com.example.parkingslot.webConnect.retrofit.RetrofitService
import com.google.gson.Gson
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.time.LocalDate

@Composable
fun HomePage(navController: NavController, modifier: Modifier = Modifier) {
    val context = LocalContext.current
    val sharedPref = remember { context.getSharedPreferences("loginPref", Context.MODE_PRIVATE) }
    val userId = sharedPref.getInt("user_id",1)
    val name = sharedPref.getString("name","");
    var showConfirmationDialog by remember { mutableStateOf(false) }

    var parkingAreas: List<ParkingAreaResponse> = ArrayList<ParkingAreaResponse>()

    //load data to display the parking slots available for the user



    ConfirmPopUp(
        showDialog = showConfirmationDialog,
        onDismiss = { showConfirmationDialog = false },
        onConfirm = {
            val sharedPref = context.getSharedPreferences("loginPref", Context.MODE_PRIVATE)
            with(sharedPref.edit()) {
                clear()
                apply()
            }
            navController.navigate(Routes.login)
            // Handle confirm logic here
        }
    )

    PageBackground {
        Text("UserId:"+userId)
        Text("UserName:"+name)
        Box(contentAlignment = Alignment.Center) {
            Column {
                LogOutButton({
                    showConfirmationDialog = true
                })

            }



            Column {

                HomePageButton("CreateParkingArea") {
                    navController.navigate(Routes.createParkingArea)
                }




                HomePageButton("View your parkingSlots"){

                    val api = RetrofitService.getRetrofit().create(ParkingSlotApi::class.java)
                    // Create the call object first
                    val call: Call<List<ParkingAreaResponse>> = api.findParkingAreaByUser(userId)

// Now you can get the URL
                    //Toast.makeText(context, call.request().url().toString(), Toast.LENGTH_LONG).show()


                    call.enqueue(object : Callback<List<ParkingAreaResponse>> {
                        override fun onResponse(
                            call: Call<List<ParkingAreaResponse>>,
                            response: Response<List<ParkingAreaResponse>>
                        ) {
                            // Toast.makeText(context, "success "+response.body(), Toast.LENGTH_SHORT).show()
                            if (response.body() != null) {
                                //Toast.makeText(context, response.body().toString(), Toast.LENGTH_SHORT).show()
                                parkingAreas = response.body()!!

                                val parkingAreasOfUser = Uri.encode(Gson().toJson(parkingAreas))
                                navController.navigate(Routes.viewYourParkingAreas+"/"+parkingAreasOfUser)

                            } else {
                                Toast.makeText(context, "No parking area assigned", Toast.LENGTH_SHORT).show()
                            }
                        }

                        override fun onFailure(call: Call<List<ParkingAreaResponse>>, t: Throwable) {
                            Toast.makeText(context, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
                        }
                    })


                }


            }



        }
    }
}

@Composable
fun HomePageButton(text: String, onClick: () -> Unit = {}) {
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
package com.example.parkingslot.ParkingArea

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
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
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.parkingslot.background.PageBackground
import com.example.parkingslot.buttons.ForwardButton
import com.example.parkingslot.webConnect.retrofit.ParkingSlotApi
import com.example.parkingslot.webConnect.retrofit.RetrofitService
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

import androidx.compose.material3.Icon
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import com.example.parkingslot.Route.Routes
import com.example.parkingslot.webConnect.requestresponse.ParkingAreaSlotRequest
import com.example.parkingslot.webConnect.requestresponse.ParkingAreaSlotResponse

@Composable
fun AddSlotsToParkingArea(
    navController: NavController, modifier: Modifier = Modifier,
    parkingAreaId: String?
) {
    val context = LocalContext.current
    val sharedPref = remember { context.getSharedPreferences("loginPref", Context.MODE_PRIVATE) }
    val userId = sharedPref.getInt("user_id",1)
    val name = sharedPref.getString("name","");


    PageBackground {
        Text("UserId:"+userId)
        Text("UserName:"+name)
        var name by remember { mutableStateOf("") }


        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("addingslots", style = MaterialTheme.typography.headlineMedium)

            Spacer(modifier = Modifier.height(16.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Name") },
                    modifier = Modifier
                        .weight(1f) // takes up remaining width
                        .padding(end = 8.dp) // space between text field & button
                )

                Button(
                    onClick = {
                        val api = RetrofitService.getRetrofit().create(ParkingSlotApi::class.java)
                        val parkingAreaSlotRequest = ParkingAreaSlotRequest( p_id = Integer.valueOf(parkingAreaId), s_name = name)
                        Toast.makeText(context, "$parkingAreaSlotRequest", Toast.LENGTH_SHORT).show()

                        api.addSlotsToParkingArea(parkingAreaSlotRequest).enqueue(object : Callback<ParkingAreaSlotResponse> {
                            override fun onResponse(call: Call<ParkingAreaSlotResponse>, response: Response<ParkingAreaSlotResponse>) {
                                if (response.body() != null) {
                                    //navController.navigate(Routes.addUserToparkingArea)
                                    Toast.makeText(context, "added slot to parking area: ${response.body()}", Toast.LENGTH_SHORT).show()
                                }
                            }
                            override fun onFailure(call: Call<ParkingAreaSlotResponse>, t: Throwable) {
                                // Handle error
                            }
                        })
                    },
                    modifier = Modifier
                        .height(60.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.White,
                        contentColor = Color.Black
                    ),
                    border = BorderStroke(2.dp, Color.Black),
                    elevation = null
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Add") // plus icon
                    Spacer(modifier = Modifier.width(4.dp))
                }
            }


            Spacer(modifier = Modifier.height(12.dp))

            ForwardButton {
                navController.navigate(Routes.addUsersToParkingArea+"/"+parkingAreaId)
            }

            Spacer(modifier = Modifier.height(90.dp))

        }


    }

}
package com.example.parkingslot.ParkingArea

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.parkingslot.Route.Routes
import com.example.parkingslot.background.PageBackground
import com.example.parkingslot.buttons.ForwardButton
import com.example.parkingslot.webConnect.requestresponse.ParkingAreaRequest
import com.example.parkingslot.webConnect.requestresponse.ParkingAreaResponse
import com.example.parkingslot.webConnect.retrofit.ParkingSlotApi

import com.example.parkingslot.webConnect.retrofit.RetrofitService
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.time.LocalDate

@Composable
fun CreateParkingArea(navController: NavController, modifier: Modifier = Modifier) {
    val context = LocalContext.current
    val sharedPref = remember { context.getSharedPreferences("loginPref", Context.MODE_PRIVATE) }
    val userId = sharedPref.getInt("user_id",1)
    val name = sharedPref.getString("name","");
    var showConfirmationDialog by remember { mutableStateOf(false) }

    PageBackground {
        Text("UserId:"+userId)
        Text("UserName:"+name)
        var name by remember { mutableStateOf("") }
        var password by remember { mutableStateOf("") }


        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("CreateParkingArea", style = MaterialTheme.typography.headlineMedium)

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("Name") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(12.dp))

            ForwardButton {
                val api = RetrofitService.getRetrofit().create(ParkingSlotApi::class.java)
                val today = LocalDate.now().toString()
                val parkingAreaRequest = ParkingAreaRequest( name =name, userId)
                Toast.makeText(context,""+parkingAreaRequest,Toast.LENGTH_SHORT).show()
                api.createParkingArea(parkingAreaRequest).enqueue(object : Callback<ParkingAreaResponse> {

                    override fun onResponse(call: Call<ParkingAreaResponse>, response: Response<ParkingAreaResponse>) {
                        if ( response.body()!=null) {

                            navController.navigate(Routes.addSlotsToParkingArea+ "/"+ response.body()?.p_id)

                            Toast.makeText(context, "created parkingARea "+response.body(), Toast.LENGTH_SHORT).show()
                        } else {
                            //Toast.makeText(context, "Login failed", Toast.LENGTH_SHORT).show()
                        }
                    }

                    override fun onFailure(call: Call<ParkingAreaResponse>, t: Throwable) {
                        Toast.makeText(context, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
                    }
                })


            }

            Spacer(modifier = Modifier.height(90.dp))

        }


    }

}



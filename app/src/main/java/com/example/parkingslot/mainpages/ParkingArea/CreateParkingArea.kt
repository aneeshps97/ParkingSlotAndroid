package com.example.parkingslot.mainpages.ParkingArea

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Divider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.parkingslot.Route.Routes
import com.example.parkingslot.mainpages.background.PageBackground
import com.example.parkingslot.customresuables.buttons.ForwardButton
import com.example.parkingslot.customresuables.textfields.LabeledTextField
import com.example.parkingslot.webConnect.dto.parkingArea.ParkingAreaRequest
import com.example.parkingslot.webConnect.dto.parkingArea.ParkingAreaResponse
import com.example.parkingslot.webConnect.retrofit.ParkingSlotApi
import com.example.parkingslot.webConnect.retrofit.RetrofitService
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

@Composable
fun CreateParkingArea(navController: NavController, modifier: Modifier = Modifier) {
    val context = LocalContext.current
    val sharedPref = remember { context.getSharedPreferences("loginPref", Context.MODE_PRIVATE) }
    val userId = sharedPref.getInt("user_id", 0)
    var name by remember { mutableStateOf("") }
    val isValidationSuccess by remember {
        derivedStateOf {
            name.isNotEmpty()
        }
    }
    PageBackground {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                "CREATE PARKING AREA", style = TextStyle(
                    fontSize = 40.sp, fontFamily = FontFamily.Monospace
                )
            )
            Spacer(modifier = Modifier.height(10.dp))

            Divider(
                modifier = Modifier
                    .fillMaxWidth(0.6f)
                    .height(1.5.dp), color = Color.Black
            )

            Spacer(modifier = Modifier.height(50.dp))
            LabeledTextField(value = name, onValueChange = { name = it }, label = "Name")
            Spacer(modifier = Modifier.height(12.dp))
            ForwardButton(isEnabled = isValidationSuccess, onClick = {createNewParking(
                context = context,
                name = name,
                userId = userId,
                navController = navController
            )})
            Spacer(modifier = Modifier.height(90.dp))

        }

    }

}

fun createNewParking(context: Context, name: String, userId: Int, navController: NavController) {
    val api = RetrofitService.getRetrofit().create(ParkingSlotApi::class.java)
    val parkingAreaRequest = ParkingAreaRequest(name = name, adminId = userId)
    api.createParkingArea(parkingAreaRequest).enqueue(object : Callback<ParkingAreaResponse> {
        override fun onResponse(
            call: Call<ParkingAreaResponse>,
            response: Response<ParkingAreaResponse>
        ) {
            if (response.body() != null && response.body()?.status == 0) {
                val parkingAreaId = response.body()?.data?.parkingAreaId
                val parkingAreaName = response.body()?.data?.name
                navController.navigate(Routes.addSlotsToParkingArea + "/$parkingAreaId/$parkingAreaName")
            } else {
                Toast.makeText(
                    context, "Failed to create " + response.body()?.message, Toast.LENGTH_SHORT
                ).show()
            }
        }

        override fun onFailure(call: Call<ParkingAreaResponse>, t: Throwable) {
            Toast.makeText(context, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
        }
    })


}



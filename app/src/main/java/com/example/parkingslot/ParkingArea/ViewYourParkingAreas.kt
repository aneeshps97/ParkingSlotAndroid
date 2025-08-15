package com.example.parkingslot.ParkingArea

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
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
import com.example.parkingslot.webConnect.requestresponse.ParkingAreaResponse

@Composable
fun ViewYourParkingAreas(
    navController: NavController, modifier: Modifier = Modifier,
    parkingAreas: List<ParkingAreaResponse> =emptyList<ParkingAreaResponse>()
) {
    val context = LocalContext.current
    val sharedPref = remember { context.getSharedPreferences("loginPref", Context.MODE_PRIVATE) }
    val userId = sharedPref.getInt("user_id",1)
    val name = sharedPref.getString("name","");


    PageBackground {
        Text("UserId:"+userId)
        Text("UserName:"+name)
        var id by remember { mutableStateOf("") }


        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("Your parkingAreas", style = MaterialTheme.typography.headlineMedium)

            parkingAreas.forEach { area ->
                ParkingSlots(area.name,{navController.navigate(Routes.welcomePage+"/"+area.p_id)})
            }

        }


    }

}

@Composable
fun ParkingSlots(text: String, onClick: () -> Unit = {}) {
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
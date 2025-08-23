package com.example.parkingslot.mainpages.ParkingArea

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Divider
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.parkingslot.Route.Routes
import com.example.parkingslot.customresuables.buttons.ForwardButton
import com.example.parkingslot.customresuables.scrollables.ScrollableBoxContent
import com.example.parkingslot.customresuables.textfields.InputTextFieldWithButton
import com.example.parkingslot.mainpages.background.PageBackground
import com.example.parkingslot.webConnect.dto.parkingArea.ParkingAreaResponse
import com.example.parkingslot.webConnect.dto.parkingArea.Slot
import com.example.parkingslot.webConnect.retrofit.ParkingSlotApi
import com.example.parkingslot.webConnect.retrofit.RetrofitService
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

@Composable
fun AddSlotsToParkingArea(
    navController: NavController,
    modifier: Modifier = Modifier,
    parkingAreaId: String?,
    parkingAreaName:String?,
    adminId:String?,
) {

    var slotName by remember { mutableStateOf("") }
    var listOfSlots by remember { mutableStateOf(mutableListOf<String>()) }
    val context = LocalContext.current
    PageBackground {
        Box(modifier = Modifier.fillMaxSize()) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.TopCenter),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(parkingAreaName.toString().toUpperCase(), style = MaterialTheme.typography.headlineMedium)
                Spacer(modifier = Modifier.height(30.dp))

                Text(
                    text = "ADD SLOT",
                    style = MaterialTheme.typography.headlineMedium.copy(fontSize = 20.sp)
                )

                Divider(modifier = Modifier
                    .fillMaxWidth(0.6f)
                    .height(1.5.dp), color = Color.Black)
                Spacer(modifier = Modifier.height(16.dp))

                //add data to a list and then in the forward button add all to the db
                InputTextFieldWithButton(slotName, onValueChange = { slotName = it }, {
                    if (slotName.isNotBlank()) {
                        listOfSlots = (listOfSlots + slotName) as MutableList<String>   // add to list
                        slotName = ""                         // clear text field
                    }
                },"Name")
                ScrollableBoxContent(listOfSlots,onRemove = { index ->
                    listOfSlots = listOfSlots.toMutableList().also { it.removeAt(index) }
                })

            }



            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.BottomCenter),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                ForwardButton(
                    isEnabled = true,
                    onClick = {saveSlotsDataToDb(navController,parkingAreaId,listOfSlots,context,parkingAreaName,adminId)},
                    alignment = Alignment.BottomEnd,
                )
            }
        }
    }

}

fun saveSlotsDataToDb(navController: NavController,parkingAreaId:String?,listOfSlots: List<String>,context: Context,parkingAreaName:String?,adminId: String?){

    val api = RetrofitService.getRetrofit().create(ParkingSlotApi::class.java)
    val request = createSlotObjectFromTheData(listOfSlots)
    api.addSlotsToParkingArea(id=Integer.parseInt(parkingAreaId),request).enqueue(object : Callback<ParkingAreaResponse> {
        override fun onResponse(
            call: Call<ParkingAreaResponse>,
            response: Response<ParkingAreaResponse>
        ) {
            if (response.body() != null && response.body()?.status == 0) {
               navController.navigate("${Routes.addUsersToParkingArea}/$parkingAreaId/$parkingAreaName/$adminId")
            } else {
                Toast.makeText(
                    context, "" + response.body()?.message, Toast.LENGTH_SHORT
                ).show()
            }
        }
        override fun onFailure(call: Call<ParkingAreaResponse>, t: Throwable) {
            Toast.makeText(context, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
        }
    })

}

fun createSlotObjectFromTheData(slotList: List<String>): MutableList<Slot> {
    return slotList.map { Slot(name = it) }.toMutableList()
}









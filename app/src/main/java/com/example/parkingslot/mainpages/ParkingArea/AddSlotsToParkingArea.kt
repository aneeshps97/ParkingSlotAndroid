package com.example.parkingslot.mainpages.ParkingArea

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.parkingslot.mainpages.background.PageBackground
import com.example.parkingslot.customresuables.buttons.ForwardButton
import com.example.parkingslot.webConnect.retrofit.ParkingSlotApi
import com.example.parkingslot.webConnect.retrofit.RetrofitService
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

import androidx.compose.material3.Icon
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Divider
import androidx.compose.material3.IconButton
import androidx.compose.ui.Alignment
import androidx.compose.ui.unit.sp
import com.example.parkingslot.Route.Routes
import com.example.parkingslot.webConnect.dto.parkingArea.ParkingAreaRequest
import com.example.parkingslot.webConnect.dto.parkingArea.ParkingAreaResponse
import com.example.parkingslot.webConnect.dto.parkingArea.Slot
import com.google.gson.Gson

@Composable
fun AddSlotsToParkingArea(
    navController: NavController,
    modifier: Modifier = Modifier,
    parkingAreaId: String?,
    parkingAreaName:String?
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
                })
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
                    onClick = {saveDataToDb(navController,parkingAreaId,listOfSlots,context)},
                    alignment = Alignment.BottomEnd,
                )
            }
        }
    }

}

fun saveDataToDb(navController: NavController,parkingAreaId:String?,listOfSlots: List<String>,context: Context){

    val api = RetrofitService.getRetrofit().create(ParkingSlotApi::class.java)
    val request = createSlotObjectFromTheData(listOfSlots)
    api.addSlotsToParkingArea(id=Integer.parseInt(parkingAreaId),request).enqueue(object : Callback<ParkingAreaResponse> {
        override fun onResponse(
            call: Call<ParkingAreaResponse>,
            response: Response<ParkingAreaResponse>
        ) {
            if (response.body() != null && response.body()?.status == 0) {

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



    navController.navigate("${Routes.addUsersToParkingArea}/$parkingAreaId")
}

fun createSlotObjectFromTheData(slotList: List<String>): MutableList<Slot> {
    return slotList.map { Slot(name = it) }.toMutableList()
}


@Composable
fun InputTextFieldWithButton(
    slotName: String,
    onValueChange: (String) -> Unit,
    onAddClick: () -> Unit
) {
    OutlinedTextField(
        value = slotName,
        onValueChange = onValueChange,
        label = { Text("Name") },
        modifier = Modifier
            .padding(end = 8.dp),
        trailingIcon = {
            IconButton(
                onClick = onAddClick,
                modifier = Modifier
                    .background(Color.Black, shape = RoundedCornerShape(8.dp))
                    .size(36.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Add Slot",
                    tint = Color.White
                )
            }
        },
        singleLine = true
    )
}

@Composable
fun LabelWithTrailingIcon(
    label: String,
    value: String,
    onRemoveClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(end = 8.dp)
            .background(Color(0xFFF0F0F0), shape = RoundedCornerShape(8.dp))
            .padding(horizontal = 12.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(text = label, style = MaterialTheme.typography.labelSmall, color = Color.Gray)
            Text(text = value, style = MaterialTheme.typography.bodyLarge)
        }

        IconButton(
            onClick = onRemoveClick,
            modifier = Modifier
                .background(Color.Black, shape = RoundedCornerShape(6.dp))
                .size(28.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Close,
                contentDescription = "Remove",
                tint = Color.White
            )
        }
    }
}




@Composable
fun ScrollableBoxContent(listOfSlots: List<String>,
                         onRemove: (Int) -> Unit) {
    val scrollState = rememberScrollState()
    Box(
        modifier = Modifier
            .fillMaxHeight(.8f)
            .verticalScroll(scrollState)
            .padding(16.dp)
    ) {
        Column {listOfSlots.forEachIndexed { index, slot ->
            LabelWithTrailingIcon(
                "",
                slot,
                { onRemove(index) }   // call back to parent
            )
            Spacer(modifier = Modifier.height(8.dp))
            }
        }
    }
}



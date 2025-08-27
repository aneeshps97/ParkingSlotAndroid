package com.example.parkingslot.mainpages.ParkingArea

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.parkingslot.customresuables.buttons.ForwardButton
import com.example.parkingslot.customresuables.textfields.LabeledTextField
import com.example.parkingslot.mainpages.background.PageBackground
import com.example.parkingslot.webConnect.dto.user.ParkingAreaData

@Composable
fun EditParkingArea(
    navController: NavController,
    modifier: Modifier = Modifier,
    ParkingAreaId: Int
) {
    var name by remember { mutableStateOf("") }
    PageBackground {
        Column(
            modifier = Modifier
                .fillMaxSize()
        ) {
            Box(
                modifier = Modifier.fillMaxSize()
            ) {
                Text(
                    "EDIT",
                    modifier = Modifier.align(Alignment.TopCenter)
                )

                Box(
                    modifier = Modifier
                        .align(Alignment.Center)
                        .fillMaxWidth()
                        .padding(16.dp)
                ){
                    LabeledTextField(value = name, onValueChange = { name = it }, label = "Name")
                }

               Box(modifier= Modifier.align(Alignment.BottomCenter)){
                   ForwardButton(onClick = {}, alignment = Alignment.BottomEnd)
                   Spacer(modifier = Modifier.height(90.dp))
               }
            }

        }

    }
}


@Preview(showBackground = true)
@Composable
fun EditParkingAreaPreview() {
    // Dummy NavController for preview
    val dummyNavController = rememberNavController()
    EditParkingArea(
        navController = dummyNavController,
        modifier = Modifier.fillMaxSize(),
        ParkingAreaId = 2
    )
}

package com.example.parkingslot.mainpages.ParkingArea

import android.content.Context
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.testing.TestNavHostController
import com.example.parkingslot.Route.Routes
import com.example.parkingslot.mainpages.background.PageBackground
import com.example.parkingslot.webConnect.dto.parkingArea.ParkingAreaData

@Composable
fun ViewYourParkingAreas(
    navController: NavController,
    modifier: Modifier = Modifier,
    parkingAreas: List<ParkingAreaData> = emptyList()
) {
    val context = LocalContext.current
    val sharedPref = remember { context.getSharedPreferences("loginPref", Context.MODE_PRIVATE) }
    val userId = sharedPref.getInt("user_id", -1)
    val name = sharedPref.getString("name", "")

    PageBackground {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            // Top Text
            Text(
                "PARKING AREAS",
                style = MaterialTheme.typography.headlineMedium,
                modifier = Modifier.align(Alignment.TopCenter)
            )

            // Center Box with scrollable list
            Box(
                modifier = Modifier
                    .align(Alignment.Center)
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(parkingAreas) { parkingArea ->
                        ParkingAreas(
                            text = parkingArea.name ?: "Unnamed Area",
                            onClick = { navController.navigate(Routes.parkingArea+"/"+parkingArea.parkingAreaId+"/"+parkingArea.name+"/"+parkingArea.adminId)},
                            onEditClick = {
                                navController.navigate(Routes.editParkingArea+"/"+parkingArea.parkingAreaId+"/"+parkingArea.name+"/"+parkingArea.adminId)
                            }
                        )
                    }
                }
            }
        }
    }
}


@Composable
fun ParkingAreas(
    text: String,
    onClick: () -> Unit = {},
    onEditClick: () -> Unit = {},
    onDeleteClick: () -> Unit ={}
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(60.dp)
            .padding(2.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Button(
            onClick = onClick,
            modifier = Modifier
                .weight(1f)
                .height(60.dp),
            shape = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color.White,
                contentColor = Color.Black
            ),
            border = BorderStroke(2.dp, Color.Black),
            elevation = null
        ) {
            Text(
                text = text,
                fontSize = 18.sp,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewViewYourParkingAreas() {
    val sampleParkingAreas = listOf(
        ParkingAreaData(parkingAreaId = 1, name = "Parking Area A",2,emptyList(),emptyList()),
    )
    val navController = TestNavHostController(LocalContext.current)
    ViewYourParkingAreas(
        navController = navController,
        parkingAreas = sampleParkingAreas
    )
}

@Preview(showBackground = true)
@Composable
fun PreviewParkingAreas() {
    ParkingAreas(
        text = "Sample Parking Area",
        onClick = {},
        onEditClick = {}
    )
}
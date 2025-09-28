package com.example.parkingslot.mainpages.ParkingArea

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.testing.TestNavHostController
import com.example.parkingslot.Route.Routes
import com.example.parkingslot.customresuables.confirm.ConfirmPopUp
import com.example.parkingslot.mainpages.background.PageBackground
import com.example.parkingslot.webConnect.dto.parkingArea.ParkingAreaData
import com.example.parkingslot.webConnect.repository.ParkingAreaRepository
import com.example.parkingslot.webConnect.repository.UserRepository

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ViewYourParkingAreas(
    navController: NavController,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val sharedPref = remember { context.getSharedPreferences("loginPref", Context.MODE_PRIVATE) }
    val userId = sharedPref.getInt("user_id", -1)
    val name = sharedPref.getString("name", "")
    var parkingAreas by remember { mutableStateOf<List<ParkingAreaData>>(emptyList()) }
    var expanded by remember { mutableStateOf(false) }
    var showConfirmationDialog by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        handleFindingUserDetailsById(
            context = context,
            userId = userId,
            repository = UserRepository()
        ) { fetchedAreas ->
            parkingAreas = fetchedAreas
        }
    }

    PageBackground {

        ConfirmPopUp(
            text1 = "Hi $name",
            text2 = "Are you sure you want to logout?",
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
            })

        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        "ParkingSlot",
                        style = MaterialTheme.typography.headlineSmall.copy(
                            fontWeight = FontWeight.SemiBold
                        )
                    )

                    Box() {

                        IconButton(
                            onClick = { expanded = true },
                        ) {
                            Icon(
                                imageVector = Icons.Default.MoreVert,
                                contentDescription = "More Options"
                            )
                        }

                        DropdownMenu(
                            expanded = expanded,
                            onDismissRequest = {
                                // This is called when the user clicks outside the menu or presses the back button
                                expanded = false
                            },
                            Modifier.background(color = Color.White)
                        ) {
                            // First menu item
                            DropdownMenuItem(
                                text = { Text("Create new") },
                                onClick = {
                                    navController.navigate(Routes.createParkingArea)
                                    expanded = false // Hide the menu after the click
                                },
                                trailingIcon = {
                                    Icon(
                                        imageVector = Icons.Default.Add,
                                        contentDescription = "Create new",
                                    )
                                }
                            )

                            // Second menu item
                            DropdownMenuItem(
                                text = { Text("Log out") },
                                onClick = {
                                    showConfirmationDialog = true
                                    expanded = false // Hide the menu after the click
                                },
                                trailingIcon = {
                                    Icon(
                                        imageVector = Icons.Default.ExitToApp,
                                        contentDescription = "Create new",
                                    )
                                }
                            )
                        }
                    }
                }

            }
            if(parkingAreas.size>0){
                // Center Box with scrollable list
                Box(
                    modifier = Modifier
                        .align(Alignment.Center)
                        .fillMaxWidth()
                        .fillMaxHeight(0.8f)
                        .padding(16.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .shadow(
                                elevation = 8.dp, // Adjust this value to change the shadow depth
                                shape = RoundedCornerShape(16.dp)
                            )
                            .background(Color.White, shape = RoundedCornerShape(16.dp))
                            .fillMaxWidth()
                            .padding(
                                horizontal = 16.dp,
                                vertical = 24.dp
                            ).align(Alignment.Center)
                    ) {

                        LazyColumn(
                            verticalArrangement = Arrangement.spacedBy(8.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            items(parkingAreas) { parkingArea ->
                                ParkingAreas(
                                    pakringAreaName = parkingArea.name ?: "Unnamed Area",
                                    onClick = {
                                        navController.navigate(Routes.parkingArea + "/" + parkingArea.parkingAreaId + "/" + parkingArea.name + "/" + parkingArea.adminId + "/" + parkingArea.ticketLine1 + "/" + parkingArea.ticketLine2)
                                    },
                                    onEditClick = {
                                        navController.navigate(Routes.editParkingArea + "/" + parkingArea.parkingAreaId + "/" + parkingArea.name + "/" + parkingArea.adminId + "/" + parkingArea.ticketLine1 + "/" + parkingArea.ticketLine2)
                                    }
                                )
                            }
                        }

                    }


                }

            }

        }
    }
}

@Composable
fun ParkingAreas(
    pakringAreaName: String,
    onClick: () -> Unit = {},
    onEditClick: () -> Unit = {},
    onDeleteClick: () -> Unit = {}
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(80.dp)
            .padding(2.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Button(
            onClick = onClick,
            modifier = Modifier
                .weight(1f)
                .height(80.dp),
            shape = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color.White,
                contentColor = Color.Black
            ),
            elevation = ButtonDefaults.buttonElevation(defaultElevation = 4.dp)
        ) {
            Text(
                text = pakringAreaName,
                fontSize = 18.sp,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}


fun handleFindingUserDetailsById(
    context: Context,
    userId: Int,
    repository: UserRepository,
    onResult: (List<ParkingAreaData>) -> Unit
) {
    repository.findUserDetailsByIdRepository(userId) { result ->
        result.onSuccess { userResponse ->
            val parkingAreas = userResponse.data?.parkingAreas ?: emptyList()
            onResult(parkingAreas)  // ✅ return parkingAreas via callback
        }
        result.onFailure { error ->
            Toast.makeText(
                context,
                error.message ?: "Failed to fetch user details",
                Toast.LENGTH_SHORT
            ).show()
            onResult(emptyList()) // return empty list on failure to avoid hanging
        }
    }
}


@Preview(showBackground = true)
@Composable
fun PreviewParkingAreas() {
    ParkingAreas(
        pakringAreaName = "Sample Parking Area",
        onClick = {},
        onEditClick = {}
    )
}



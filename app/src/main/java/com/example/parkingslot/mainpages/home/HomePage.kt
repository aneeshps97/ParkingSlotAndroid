package com.example.parkingslot.mainpages.home

import android.content.Context
import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
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
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.parkingslot.Route.Routes
import com.example.parkingslot.customresuables.buttons.LogOutButton
import com.example.parkingslot.customresuables.confirm.ConfirmPopUp
import com.example.parkingslot.mainpages.background.PageBackground
import com.example.parkingslot.webConnect.dto.parkingArea.ParkingAreaResponse
import com.example.parkingslot.webConnect.dto.user.UserResponse
import com.example.parkingslot.webConnect.retrofit.ParkingSlotApi
import com.example.parkingslot.webConnect.retrofit.RetrofitService
import com.google.gson.Gson
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

@Composable
fun HomePage(navController: NavController, modifier: Modifier = Modifier) {
    val context = LocalContext.current
    val sharedPref = remember { context.getSharedPreferences("loginPref", Context.MODE_PRIVATE) }
    val userId = sharedPref.getInt("user_id", 1)
    val name = sharedPref.getString("name", "");
    var showConfirmationDialog by remember { mutableStateOf(false) }

    var parkingAreas: List<ParkingAreaResponse> = ArrayList<ParkingAreaResponse>()

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


    PageBackground {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Logout button at top-right
            Row(
                modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End
            ) {
                LogOutButton {
                    showConfirmationDialog = true
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Welcome text
            Text(
                text = "WELCOME", style = TextStyle(
                    fontSize = 40.sp, fontFamily = FontFamily.Monospace
                )
            )

            Divider(
                modifier = Modifier
                    .fillMaxWidth(0.6f)
                    .height(1.dp), color = Color.Black
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Buttons and divider
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(24.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                HomePageButton(
                    icon = {
                        Icon(
                            Icons.Default.Add,
                            contentDescription = "Add",
                            modifier = Modifier.size(120.dp)
                        )
                    }, label = "ADD"
                ) {
                    navController.navigate(Routes.createParkingArea)
                }

                Divider(
                    modifier = Modifier
                        .fillMaxWidth(0.6f)
                        .height(1.dp), color = Color.Black
                )

                HomePageButton(
                    icon = {
                        Icon(
                            Icons.Default.ExitToApp,
                            contentDescription = "View",
                            modifier = Modifier.size(120.dp)
                        )
                    }, label = "VIEW"
                ) {
                    val api = RetrofitService.getRetrofit().create(ParkingSlotApi::class.java)
                    val call: Call<UserResponse> = api.findUserDetailsById(userId)
                    call.enqueue(object : Callback<UserResponse> {
                        override fun onResponse(
                            call: Call<UserResponse>,
                            response: Response<UserResponse>
                        ) {
                            if (response.body() != null && response.body()?.status == 0) {
                                val parkingAreas = response.body()?.data?.parkingAreas
                                navController.currentBackStackEntry?.savedStateHandle?.set("parkingAreasOfUser", parkingAreas)
                                navController.navigate(Routes.viewYourParkingAreas)

                            } else {
                                Toast.makeText(
                                    context, "No parking area assigned", Toast.LENGTH_SHORT
                                ).show()
                            }
                        }

                        override fun onFailure(
                            call: Call<UserResponse>, t: Throwable
                        ) {
                            Toast.makeText(context, "Error: ${t.message}", Toast.LENGTH_SHORT)
                                .show()
                        }
                    })
                }
            }
        }
    }
}


@Composable
fun HomePageButton(
    icon: @Composable () -> Unit, label: String, onClick: () -> Unit = {}
) {
    Button(
        onClick = onClick, modifier = Modifier
            .size(220.dp) // You can increase this if needed
            .padding(4.dp), shape = RoundedCornerShape(16.dp), colors = ButtonDefaults.buttonColors(
            containerColor = Color.Transparent, contentColor = Color.Black
        ), elevation = null, border = null
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxSize()
        ) {
            icon()

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = label, fontSize = 16.sp, fontWeight = FontWeight.Medium
            )
        }
    }
}




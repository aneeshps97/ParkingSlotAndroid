package com.example.parkingslot.mainpages.ParkingArea

import android.content.Context
import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
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
import com.example.parkingslot.customresuables.confirm.ConfirmPopUp
import com.example.parkingslot.customresuables.labels.LabelWithTrailingIcon
import com.example.parkingslot.customresuables.textfields.InputTextFieldWithButton
import com.example.parkingslot.mainpages.background.PageBackground
import com.example.parkingslot.webConnect.dto.parkingArea.ParkingAreaResponse
import com.example.parkingslot.webConnect.dto.user.User
import com.example.parkingslot.webConnect.dto.user.UserResponse
import com.example.parkingslot.webConnect.retrofit.ParkingSlotApi
import com.example.parkingslot.webConnect.retrofit.RetrofitService
import com.google.gson.Gson
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


@Composable
fun AddUsersToParkingArea(
    navController: NavController,
    modifier: Modifier = Modifier,
    parkingAreaId: String?,
    parkingAreaName: String?,
    adminId:String?,
) {
    var email by remember { mutableStateOf("") }
    val listOfUsers = remember { mutableStateListOf<User>() }
    val context = LocalContext.current
    val showConfirmationDialog = remember { mutableStateOf(false) }
    val user = remember { mutableStateOf<User?>(null) }
    ConfirmPopUp(
        text1 = user.value?.name ?: "",
        text2 = "sure you want to add this user?",
        showDialog = showConfirmationDialog.value,
        onDismiss = { showConfirmationDialog.value = false },
        onConfirm = {
            user.value?.let {
                listOfUsers.add(it)
            }
            showConfirmationDialog.value = false
        }
    )
    PageBackground {
        Box(modifier = Modifier.fillMaxSize()) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.TopCenter),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    parkingAreaName.toString().toUpperCase(),
                    style = MaterialTheme.typography.headlineMedium
                )
                Spacer(modifier = Modifier.height(30.dp))

                Text(
                    text = "ADD USER",
                    style = MaterialTheme.typography.headlineMedium.copy(fontSize = 20.sp)
                )

                Divider(
                    modifier = Modifier
                        .fillMaxWidth(0.6f)
                        .height(1.5.dp), color = Color.Black
                )
                Spacer(modifier = Modifier.height(16.dp))

                //add data to a list and then in the forward button add all to the db
                // when the + button is clicked an api should trigger and fetch the data of the user based on the email
                // if not found we will show a popup saying user not found we can have confirm popup for that
                InputTextFieldWithButton(email, onValueChange = { email = it }, {
                    handleFindingUserByEmail(context,email,listOfUsers,showConfirmationDialog,user)
                },"Email")
                ScrollableBoxContentForUsers(listOfUsers, onRemove = { index ->
                    listOfUsers.removeAt(index)
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
                    onClick = { saveUserDataToDb(navController, parkingAreaId, listOfUsers, context,parkingAreaName,adminId)},
                    alignment = Alignment.BottomEnd,
                )
            }
        }
    }

}


fun saveUserDataToDb(navController: NavController,parkingAreaId:String?,listOfUsers: List<User>,context: Context,parkingAreaName:String?,adminId:String?){
    val api = RetrofitService.getRetrofit().create(ParkingSlotApi::class.java)
    val request = createListOfUserIdsFromData(listOfUsers, adminId)
    api.addUsersToParkingArea(id=Integer.parseInt(parkingAreaId),request).enqueue(object : Callback<ParkingAreaResponse> {
        override fun onResponse(
            call: Call<ParkingAreaResponse>,
            response: Response<ParkingAreaResponse>
        ) {
            if (response.body() != null && response.body()?.status == 0) {
               val json = Uri.encode(Gson().toJson(response.body()?.data))
                navController.navigate(Routes.assignSlotForUsers+ "/$json")
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

fun createListOfUserIdsFromData(listOfUsers: List<User>, adminId: String?): List<Int> {
    val listOfUserIds = mutableListOf<Int>()
    for (user in listOfUsers) {
        listOfUserIds.add(user.id ?: 0)
    }
    //adding admin as a default user so that he can view the data in the parking area
    //and this will make editing easier
    listOfUserIds.add(Integer.parseInt(adminId))
    return listOfUserIds
}

@Composable
fun ScrollableBoxContentForUsers(listofData: List<User>,
                                 onRemove: (Int) -> Unit) {
    val scrollState = rememberScrollState()
    Box(
        modifier = Modifier
            .fillMaxHeight(.8f)
            .verticalScroll(scrollState)
            .padding(16.dp)
    ) {
        Column {listofData.forEachIndexed { index, data ->
            LabelWithTrailingIcon(
                data.name,
                { onRemove(index) }   // call back to parent
            )
            Spacer(modifier = Modifier.height(8.dp))
        }
        }
    }
}


fun handleFindingUserByEmail(
    context: Context,
    email: String, listOfUsers: MutableList<User>,
    showConfirmationDialog: MutableState<Boolean>,
    userState: MutableState<User?>
) {
    val api = RetrofitService.getRetrofit().create(ParkingSlotApi::class.java)
    api.findUserByEmail(email=email).enqueue(object : Callback<UserResponse> {
        override fun onResponse(
            call: Call<UserResponse>,
            response: Response<UserResponse>
        ) {
            if (response.body() != null && response.body()?.status == 0) {
                val userName = response.body()?.data?.name
                val userId = response.body()?.data?.userId
                val user = User(id = userId, name = userName.toString())
                userState.value = user
                showConfirmationDialog.value = true

            } else {
                Toast.makeText(
                    context, "" + response.body()?.message, Toast.LENGTH_SHORT
                ).show()
            }
        }
        override fun onFailure(call: Call<UserResponse>, t: Throwable) {
            Toast.makeText(context, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
        }
    })

}

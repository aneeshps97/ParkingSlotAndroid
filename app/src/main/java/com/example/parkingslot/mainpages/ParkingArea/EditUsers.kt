package com.example.parkingslot.mainpages.ParkingArea

import android.content.Context
import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.parkingslot.Route.Routes
import com.example.parkingslot.customresuables.confirm.ConfirmPopUp
import com.example.parkingslot.customresuables.labels.LabelWithTrailingIcon
import com.example.parkingslot.customresuables.popUp.AddUserPopUp
import com.example.parkingslot.mainpages.background.PageBackground
import com.example.parkingslot.webConnect.dto.parkingArea.ParkingAreaData
import com.example.parkingslot.webConnect.dto.parkingArea.ParkingAreaResponse
import com.example.parkingslot.webConnect.dto.slot.SlotData
import com.example.parkingslot.webConnect.dto.user.User
import com.example.parkingslot.webConnect.dto.user.UserData
import com.example.parkingslot.webConnect.dto.user.UserResponse
import com.example.parkingslot.webConnect.retrofit.ParkingSlotApi
import com.example.parkingslot.webConnect.retrofit.RetrofitService
import com.google.gson.Gson
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

@Composable
fun EditUsers(
    navController: NavController,
    modifier: Modifier = Modifier,
    parkingAreaData: ParkingAreaData
) {
    val listOfUsers = remember(parkingAreaData) {
        mutableStateListOf<UserData>().apply { addAll(parkingAreaData.users) }
    }
    val showRemoveUser = remember { mutableStateOf(false) }
    val showAddUser = remember { mutableStateOf(false) }

    val userToRemove = remember { mutableStateOf(UserData(
        0, "",
         "",
        "",
         "",
         emptyList()
    )) }
    val context: Context = LocalContext.current
    val indexToRemove = remember { mutableStateOf(-1) }
    val indexToUpdate = remember { mutableStateOf(-1) }

    ConfirmPopUp(
        showDialog = showRemoveUser.value,
        onDismiss = { showRemoveUser.value = false },
        onConfirm = {
            showRemoveUser.value = false;
            if (indexToRemove.value in listOfUsers.indices) {
                removeUserFromParkingArea( context, listOfUsers, indexToRemove.value,parkingAreaData.parkingAreaId)
            }
        },
        "Remove "+userToRemove.value.name,
        "Are you sure?"
    )

    PageBackground {


        AddUserPopUp(
            showDialog = showAddUser.value,
            onDismiss = {showAddUser.value=false},
            navController = navController,
            onUserAdded = {user->handleUserAdded(user,context,parkingAreaData.parkingAreaId,listOfUsers)}
        )

        Column ( modifier= modifier.fillMaxSize()){
            Box(Modifier.fillMaxSize()){
                Text(
                    "EDIT",

                    modifier = Modifier
                        .align(Alignment.TopCenter)
                        .padding(top = 16.dp),
                    style = MaterialTheme.typography.headlineSmall.copy(
                        fontWeight = FontWeight.SemiBold
                    )
                )

                Box(
                    modifier = Modifier
                        .align(Alignment.Center)
                        .fillMaxWidth()
                        .padding(16.dp)
                ){
                    //populate the slots in a list of text fileds and with an edit and x button
                    //when edit is clicked it should display on a popup where we can edit the details of it
                    //also add a button to add slots
                    //just copy and paste the code for add slots

                    ScrollableBoxContentForEditUsers(listOfUsers,onRemove = { index ->
                        showRemoveUser.value = true
                        userToRemove.value=listOfUsers.get(index)
                        indexToRemove.value = index
                    })


                }

                Box(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(bottom = 32.dp)
                ) {
                    Column {

                        Button(
                            onClick = {showAddUser.value=true},
                            modifier = Modifier
                                .height(60.dp)
                                .fillMaxWidth(0.9f),
                            shape = RoundedCornerShape(16.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color.White,
                                contentColor = Color.Black
                            ),
                            border = BorderStroke(2.dp, Color.Black),
                            elevation = null
                        ) {
                            Text(
                                text = "Add user",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }

                        Spacer(modifier.height(20.dp))

                        Button(
                            onClick = {navController.navigate(Routes.editParkingArea+"/"+parkingAreaData.parkingAreaId+"/"+parkingAreaData.name)},
                            modifier = Modifier
                                .height(60.dp)
                                .fillMaxWidth(0.9f),
                            shape = RoundedCornerShape(16.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color.White,
                                contentColor = Color.Black
                            ),
                            border = BorderStroke(2.dp, Color.Black),
                            elevation = null
                        ) {
                            Text(
                                text = "Finish",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }

                    }

                }
            }

        }

    }
}

@Composable
fun ScrollableBoxContentForEditUsers(listofData: List<UserData>,
                                     onRemove: (Int) -> Unit
) {
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
                { onRemove(index) } ,
                false,
                {}
            )
            Spacer(modifier = Modifier.height(8.dp))
        }
        }
    }
}


fun handleUserAdded(user: User, context: Context, parkingAreaId:Int,listOfUsers: SnapshotStateList<UserData>){
    val listOfUserIds = mutableListOf<Int>()
    listOfUserIds.add(user.id!!)
    val api = RetrofitService.getRetrofit().create(ParkingSlotApi::class.java)
    api.addUsersToParkingArea(parkingAreaId,listOfUserIds).enqueue(object : Callback<ParkingAreaResponse> {
        override fun onResponse(
            call: Call<ParkingAreaResponse>,
            response: Response<ParkingAreaResponse>
        ) {
            if (response.body() != null && response.body()?.status == 0) {
                Toast.makeText(context,"user Added", Toast.LENGTH_SHORT).show()
                listOfUsers.clear()

                // Repopulate the list with the new data from the response
                response.body()?.data?.users?.let {
                    listOfUsers.addAll(it)
                }
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


fun removeUserFromParkingArea(context: Context, listOfUsers: SnapshotStateList<UserData>, index: Int,parkingAreaId:Int){
    val api = RetrofitService.getRetrofit().create(ParkingSlotApi::class.java)
    api.removeUserFromParkingArea(parkingAreaId,listOfUsers.get(index).userId).enqueue(object :Callback<ParkingAreaResponse>{
        override fun onResponse(
            call: Call<ParkingAreaResponse?>,
            response: Response<ParkingAreaResponse?>
        ) {
            if(response.body()?.status==0){
                Toast.makeText(context, "user removed", Toast.LENGTH_SHORT).show()
                listOfUsers.removeAt(index)
            }
        }

        override fun onFailure(
            call: Call<ParkingAreaResponse?>,
            t: Throwable
        ) {
            TODO("Not yet implemented")
        }

    })

}


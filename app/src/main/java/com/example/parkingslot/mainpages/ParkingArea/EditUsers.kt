package com.example.parkingslot.mainpages.ParkingArea

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
import com.example.parkingslot.customresuables.popUp.TransferAdminPopUP
import com.example.parkingslot.mainpages.background.PageBackground
import com.example.parkingslot.webConnect.dto.parkingArea.ParkingAreaData
import com.example.parkingslot.webConnect.dto.parkingArea.ParkingAreaResponse
import com.example.parkingslot.webConnect.dto.user.User
import com.example.parkingslot.webConnect.dto.user.UserData
import com.example.parkingslot.webConnect.repository.ParkingAreaRepository
import com.example.parkingslot.webConnect.retrofit.ParkingSlotApi
import com.example.parkingslot.webConnect.retrofit.RetrofitService
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import com.example.parkingslot.webConnect.repository.ParkingAreaRepository.*

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

    val userToRemove = remember { mutableStateOf(UserData.EMPTY) }
    val context: Context = LocalContext.current
    val indexToRemove = remember { mutableStateOf(-1) }
    val indexToTransfer = remember { mutableStateOf(-1) }
    var showTransferDialog by remember { mutableStateOf(value = false) }
    var selectedUserIdForTransfer by remember { mutableStateOf(value = 0) }
    var showConfirmationDialogForTransferingSlot by remember { mutableStateOf(false) }
    val parkingAreaRespository: ParkingAreaRepository = ParkingAreaRepository();

    TransferAdminPopUP(
        showDialog = showTransferDialog,
        onDismiss = { showTransferDialog = false },
        navController = navController,
        userList = listOfUsers,
        onUserSelected = { selectedUserId ->
            selectedUserIdForTransfer = selectedUserId
            showConfirmationDialogForTransferingSlot = true
        }
    )

    ConfirmPopUp(
        showDialog = showConfirmationDialogForTransferingSlot,
        onDismiss = { showConfirmationDialogForTransferingSlot = false },
        onConfirm = {
            showRemoveUser.value = false;
            Toast.makeText(context, "" + selectedUserIdForTransfer, Toast.LENGTH_SHORT).show()
            transferAdmin(
                selectedUserIdForNewAdmin = selectedUserIdForTransfer,
                parkingAreaId = parkingAreaData.parkingAreaId,
                parkingAreaRespository,
                context = context,
                navController = navController
            )
        },
        "Make him Admin",
        "Are you sure?"
    )

    ConfirmPopUp(
        showDialog = showRemoveUser.value,
        onDismiss = { showRemoveUser.value = false },
        onConfirm = {
            showRemoveUser.value = false;
            if (indexToRemove.value in listOfUsers.indices) {
                removeUserFromParkingArea(
                    context,
                    listOfUsers,
                    indexToRemove.value,
                    parkingAreaData.parkingAreaId,
                    parkingAreaRespository
                )
            }
        },
        "Remove " + userToRemove.value.name,
        "Are you sure?"
    )

    PageBackground {
        AddUserPopUp(
            showDialog = showAddUser.value,
            onDismiss = { showAddUser.value = false },
            navController = navController,
            onUserAdded = { user ->
                handleUserAdded(
                    user,
                    context,
                    parkingAreaData.parkingAreaId,
                    listOfUsers, parkingAreaRespository
                )
            }
        )

        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            // Top-aligned text
            Text(
                "EDIT",
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .padding(top = 16.dp),
                style = MaterialTheme.typography.headlineSmall.copy(
                    fontWeight = FontWeight.SemiBold
                )
            )

            // Scrollable content takes up all remaining space
            ScrollableBoxContentForEditUsers(
                listOfUsers,
                onRemove = { index ->
                    userToRemove.value = listOfUsers.get(index)
                    indexToRemove.value = index
                    showRemoveUser.value =
                        checkIfAdmin(index, listOfUsers, parkingAreaData, context)
                    showTransferDialog = !showRemoveUser.value
                },
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(horizontal = 10.dp, vertical = 25.dp)
            )

            // Buttons at the bottom, correctly aligned
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.CenterHorizontally)
                    .padding(bottom = 32.dp, top = 20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Button(
                    onClick = { showAddUser.value = true },
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

                Spacer(modifier = Modifier.height(20.dp))

                Button(
                    onClick = { navController.navigate(Routes.editParkingArea + "/" + parkingAreaData.parkingAreaId + "/" + parkingAreaData.name + "/" + parkingAreaData.ticketLine1 + "/" + parkingAreaData.ticketLine2) },
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

fun transferAdmin(
    selectedUserIdForNewAdmin: Int,
    parkingAreaId: Int,
    parkingAreaRespository: ParkingAreaRepository,
    context: Context,
    navController: NavController
) {
    parkingAreaRespository.changeAdmin(
        context = context,
        parkingAreaId = parkingAreaId,
        newAdminId = selectedUserIdForNewAdmin,
        onSuccess = {
            navController.navigate(Routes.viewYourParkingAreas)
        },
        onError = { errorMessage ->
            Log.e("RemoveUser", errorMessage)
        }
    )


}

fun checkIfAdmin(
    index: Int,
    listOfUsers: SnapshotStateList<UserData>,
    parkingAreaData: ParkingAreaData,
    context: Context
): Boolean {
    if (listOfUsers.get(index).userId == parkingAreaData.adminId) {
        Toast.makeText(
            context, "" + "Admin can't be removed", Toast.LENGTH_SHORT
        ).show()
        return false
    }
    return true
}

@Composable
fun ScrollableBoxContentForEditUsers(
    listofData: List<UserData>,
    onRemove: (Int) -> Unit,
    modifier: Modifier = Modifier // Add this modifier parameter
) {
    val scrollState = rememberScrollState()
    Box(
        modifier = modifier // Apply the modifier here
            .verticalScroll(scrollState)
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            listofData.forEachIndexed { index, data ->
                LabelWithTrailingIcon(
                    data.name,
                    { onRemove(index) },
                    false,
                    {}
                )
                Spacer(modifier = Modifier.height(8.dp))
            }
        }
    }
}


fun handleUserAdded(
    user: User,
    context: Context,
    parkingAreaId: Int,
    listOfUsers: SnapshotStateList<UserData>,
    parkingAreaRespository: ParkingAreaRepository
) {

    val userExists = listOfUsers.any { userData ->
        userData.userId == user.id
    }
    if (!userExists) {
        val listOfUserIds = mutableListOf<Int>()
        listOfUserIds.add(user.id!!)
        parkingAreaRespository.addUserToParkingArea(
            context = context,
            parkingAreaId = parkingAreaId,
            userId = user.id!!,
            onSuccess = { updatedUsers ->
                listOfUsers.clear()
                listOfUsers.addAll(updatedUsers)
            },
            onError = { errorMessage ->
                Log.e("AddUser", errorMessage)
            }
        )


    } else {
        Toast.makeText(context, "User Already Added", Toast.LENGTH_SHORT).show()
    }


}


fun removeUserFromParkingArea(
    context: Context,
    listOfUsers: SnapshotStateList<UserData>,
    index: Int,
    parkingAreaId: Int,
    parkingAreaRespository: ParkingAreaRepository
) {

    parkingAreaRespository.removeUserFromParkingArea(
        context = context,
        parkingAreaId = parkingAreaId,
        userId = listOfUsers[index].userId,
        onSuccess = {
            listOfUsers.removeAt(index)
        },
        onError = { errorMessage ->
            Log.e("RemoveUser", errorMessage)
        }
    )


}


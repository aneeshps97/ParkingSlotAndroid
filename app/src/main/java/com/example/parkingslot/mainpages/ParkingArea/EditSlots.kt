package com.example.parkingslot.mainpages.ParkingArea

import android.content.Context
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
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshots.Snapshot
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.parkingslot.Route.Routes
import com.example.parkingslot.customresuables.confirm.ConfirmPopUp
import com.example.parkingslot.customresuables.labels.LabelWithTrailingIcon
import com.example.parkingslot.customresuables.popUp.AddParkingSlotPopUp
import com.example.parkingslot.customresuables.popUp.UpdateSlotDataPopUp
import com.example.parkingslot.mainpages.background.PageBackground
import com.example.parkingslot.webConnect.dto.slot.SlotData
import com.example.parkingslot.webConnect.dto.parkingArea.ParkingAreaData
import com.example.parkingslot.webConnect.dto.slot.Slot
import com.example.parkingslot.webConnect.dto.slot.SlotDataResponse
import com.example.parkingslot.webConnect.dto.user.UserData
import com.example.parkingslot.webConnect.retrofit.ParkingSlotApi
import com.example.parkingslot.webConnect.retrofit.RetrofitService
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

@Composable
fun EditSlots(
    navController: NavController,
    modifier: Modifier = Modifier,
    parkingAreaData: ParkingAreaData
) {
    val listOfSlots = remember(parkingAreaData) {
        mutableStateListOf<SlotData>().apply { addAll(parkingAreaData.slots) }
    }
    val showRemoveSlot = remember { mutableStateOf(false) }
    val showUpdateSlot = remember { mutableStateOf(false) }
    val showAddSlot = remember { mutableStateOf(false) }

    val slotToRemove = remember { mutableStateOf(SlotData(0, "")) }
    val slotToUpdate = remember { mutableStateOf(SlotData(0, "")) }
    val context: Context = LocalContext.current
    val indexToRemove = remember { mutableStateOf(-1) }
    val indexToUpdate = remember { mutableStateOf(-1) }

    ConfirmPopUp(
        showDialog = showRemoveSlot.value,
        onDismiss = { showRemoveSlot.value = false },
        onConfirm = {
                    showRemoveSlot.value = false;
            if (indexToRemove.value in listOfSlots.indices) {
                removeSlot( context, listOfSlots, indexToRemove.value)
            }
        },
        "Remove "+slotToRemove.value.name,
        "Are you sure?"
    )

    PageBackground {

        UpdateSlotDataPopUp(
            showDialog = showUpdateSlot.value,
            onDismiss = {showUpdateSlot.value=false},
            navController = navController,
            slotData = slotToUpdate.value,
            onSlotUpdated = {slotData->handleSlotUpdated(slotData,context,showUpdateSlot,listOfSlots)}
        )

        AddParkingSlotPopUp(
            showDialog = showAddSlot.value,
            onDismiss = {showAddSlot.value=false},
            navController = navController,
            onSlotAdded = {slot->handleSlotAdded(slot,context,showUpdateSlot,listOfSlots,
                parkingAreaData.parkingAreaId)}
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

                    ScrollableBoxContentForEditSlots(listOfSlots,onRemove = { index ->
                            showRemoveSlot.value = true
                            slotToRemove.value=listOfSlots.get(index)
                            indexToRemove.value = index
                    },onUpdate = {index ->
                        showUpdateSlot.value = true
                        slotToUpdate.value = listOfSlots.get(index)
                        indexToUpdate.value = index
                    })

                    
                }

                Box(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(bottom = 32.dp)
                ) {
                    Column {

                        Button(
                            onClick = {showAddSlot.value=true},
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
                                text = "Create slot",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }

                        Spacer(modifier.height(20.dp))

                        Button(
                            onClick = {navController.navigate(Routes.editParkingArea+"/"+parkingAreaData.parkingAreaId+"/"+parkingAreaData.name+"/"+parkingAreaData.ticketLine1+"/"+parkingAreaData.ticketLine2)},
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

fun handleSlotAdded(slot: Slot, context: Context, showUpdateSlot: MutableState<Boolean>, listOfSlots: MutableList<SlotData>,parkingAreaId:Int){
    val api = RetrofitService.getRetrofit().create(ParkingSlotApi::class.java)
    api.addSlots(slot,parkingAreaId).enqueue(object :Callback<SlotDataResponse>{
        override fun onResponse(
            call: Call<SlotDataResponse?>,
            response: Response<SlotDataResponse?>
        ) {
            if(response.body()?.status==0){
                Toast.makeText(context, "Slot added", Toast.LENGTH_SHORT).show()
                response.body()?.data?.let { listOfSlots.add(it) }
                showUpdateSlot.value = false
            }else{
                Toast.makeText(context, "failed", Toast.LENGTH_SHORT).show()
            }
        }

        override fun onFailure(
            call: Call<SlotDataResponse?>,
            t: Throwable
        ) {
            TODO("Not yet implemented")
        }

    })
}

fun handleSlotUpdated(slotData: SlotData,context: Context,showUpdateSlot: MutableState<Boolean>,listOfSlots: MutableList<SlotData>){
    val api = RetrofitService.getRetrofit().create(ParkingSlotApi::class.java)
    api.updateSlot(slotData,slotData.slotId).enqueue(object :Callback<SlotDataResponse>{
        override fun onResponse(
            call: Call<SlotDataResponse?>,
            response: Response<SlotDataResponse?>
        ) {
            if(response.body()?.status==0){
                Toast.makeText(context, "Slot upated", Toast.LENGTH_SHORT).show()
                listOfSlots.removeIf { slot->slot.slotId==slotData.slotId }
                listOfSlots.add(slotData)
                showUpdateSlot.value = false
            }else{
                Toast.makeText(context, "failed", Toast.LENGTH_SHORT).show()
            }
        }

        override fun onFailure(
            call: Call<SlotDataResponse?>,
            t: Throwable
        ) {
            TODO("Not yet implemented")
        }

    })
}

fun removeSlot(context: Context, listOfSlots: SnapshotStateList<SlotData>,index: Int){
    val api = RetrofitService.getRetrofit().create(ParkingSlotApi::class.java)
  api.deleteSlot(listOfSlots.get(index).slotId).enqueue(object :Callback<SlotDataResponse>{
      override fun onResponse(
          call: Call<SlotDataResponse?>,
          response: Response<SlotDataResponse?>
      ) {
          if(response.body()?.status==0){
              Toast.makeText(context, "Slot removed", Toast.LENGTH_SHORT).show()
              listOfSlots.removeAt(index)
          }
      }

      override fun onFailure(
          call: Call<SlotDataResponse?>,
          t: Throwable
      ) {
          TODO("Not yet implemented")
      }

  })

}


@Composable
fun ScrollableBoxContentForEditSlots(listofData: List<SlotData>,
                                     onRemove: (Int) -> Unit,onUpdate:(Int) -> Unit
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
                true,
                {onUpdate(index)}
            )
            Spacer(modifier = Modifier.height(8.dp))
        }
        }
    }
}


@Preview(showBackground = true)
@Composable
fun EditSlotsPreview() {
    val dummyNavController = rememberNavController()

    val dummySlots = listOf(
        SlotData(slotId = 1, name = "Slot A"),
        SlotData(slotId = 2, name = "Slot B"),
        SlotData(slotId = 3, name = "Slot C")
    )

    val dummyUsers =emptyList<UserData>()

    val dummyParkingArea = ParkingAreaData(
        parkingAreaId = 2,
        name = "Main Street Parking",
        adminId = 99,
        slots = dummySlots,
        users = dummyUsers,
        ticketLine1 = "lulu",
        ticketLine2 = "tech park"
    )

    EditSlots(
        navController = dummyNavController,
        modifier = Modifier.fillMaxSize(),
        parkingAreaData = dummyParkingArea
    )
}
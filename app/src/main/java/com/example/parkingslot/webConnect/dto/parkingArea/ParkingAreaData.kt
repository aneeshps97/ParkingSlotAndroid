package com.example.parkingslot.webConnect.dto.parkingArea

import com.example.parkingslot.webConnect.dto.slot.SlotData
import com.example.parkingslot.webConnect.dto.user.UserData

data class ParkingAreaData(
    val parkingAreaId:Int,
    val name: String,
    val adminId:Int,
    val slots: List<SlotData>,
    val users: List<UserData>
)

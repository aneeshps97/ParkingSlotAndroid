package com.example.parkingslot.webConnect.dto.user

import com.example.parkingslot.webConnect.dto.parkingArea.ParkingAreaData
import com.example.parkingslot.webConnect.dto.slot.SlotData

data class UserResponse(
    val statusCode: Int,
    val status: Int,
    val message: String,
    val data: UserData
)



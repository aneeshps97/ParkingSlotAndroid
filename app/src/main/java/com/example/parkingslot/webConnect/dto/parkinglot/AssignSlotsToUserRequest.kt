package com.example.parkingslot.webConnect.dto.parkinglot

data class AssignSlotsToUserRequest(
    val slotNo: String,
    val date: String,
    val userId: Int,
    val pid:Int
)
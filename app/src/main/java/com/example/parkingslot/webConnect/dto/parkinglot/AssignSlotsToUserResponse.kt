package com.example.parkingslot.webConnect.dto.parkinglot

data class AssignSlotsToUserResponse(
    val id:Int,
    val slotNo: String,
    val date: String,
    val userId: Int,
    val pid:Int
)
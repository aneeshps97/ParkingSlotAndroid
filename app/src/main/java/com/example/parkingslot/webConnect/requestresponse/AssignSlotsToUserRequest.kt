package com.example.parkingslot.webConnect.requestresponse

data class AssignSlotsToUserRequest(
    val slotNo: String,
    val date: String,
    val userId: Int,
    val pid:Int
)

package com.example.parkingslot.webConnect.requestresponse

data class AssignSlotsToUserResponse(
    val id:Int,
    val slotNo: String,
    val date: String,
    val userId: Int,
    val pid:Int
)

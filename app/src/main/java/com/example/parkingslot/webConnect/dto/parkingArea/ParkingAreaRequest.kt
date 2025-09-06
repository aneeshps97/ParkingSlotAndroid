package com.example.parkingslot.webConnect.dto.parkingArea

data class ParkingAreaRequest(
    val name: String,
    val ticketLine1:String,
    val ticketLine2: String,
    val adminId:Int
)

package com.example.parkingslot.webConnect.dto.booking

data class BookingRequest(
    val userId: Int,
    val parkingAreaId: Int,
    val date: String,
    val slotId: Int
)
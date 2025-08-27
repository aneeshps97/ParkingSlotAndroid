package com.example.parkingslot.webConnect.dto.booking

import com.example.parkingslot.webConnect.dto.parkingArea.ParkingAreaResponse
import com.example.parkingslot.webConnect.dto.user.UserResponse


data class BookingResponse(
    val statusCode: Int,
    val status: Int,
    val message: String,
    val data: List<BookingData>
)

data class BookingData(
    val bookingId: Int,
    val user : UserResponse,
    val parkingArea: ParkingAreaResponse,
    val date: String,
    val slot:Slot
)

data class Slot(
    val slotId:Int,
    val name:String,
)
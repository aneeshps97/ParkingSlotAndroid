package com.example.parkingslot.webConnect.dto.booking

import com.example.parkingslot.webConnect.dto.parkingArea.ParkingAreaResponse
import com.example.parkingslot.webConnect.dto.user.UserData
import com.example.parkingslot.webConnect.dto.user.UserResponse

data class BookingResponse(
    val statusCode: Int,
    val status: Int,
    val message: String,
    val data: List<BookingData>
)
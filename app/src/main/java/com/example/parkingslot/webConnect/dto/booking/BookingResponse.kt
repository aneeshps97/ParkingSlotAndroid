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

data class BookingData(
    val bookingId: Int?,                 // nullable Int
    val user: UserData?,             // nullable
    val parkingArea: ParkingAreaResponse?,
    val date: String?,                   // nullable
    val slot: Slot?                      // nullable
)

data class Slot(
    val slotId: Int?,                    // nullable Int
    val name: String?                    // nullable String
)

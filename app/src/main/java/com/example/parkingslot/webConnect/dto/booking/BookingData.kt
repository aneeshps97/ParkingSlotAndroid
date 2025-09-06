package com.example.parkingslot.webConnect.dto.booking
import com.example.parkingslot.webConnect.dto.parkingArea.ParkingAreaData
import com.example.parkingslot.webConnect.dto.parkingArea.ParkingAreaResponse
import com.example.parkingslot.webConnect.dto.user.UserData

data class BookingData(
    val bookingId: Int,
    val user: UserData,
    val parkingArea: ParkingAreaData,
    val date: String,
    val slot: Slot
) {
    companion object {
        val EMPTY = BookingData(
            bookingId = 0,
            user = UserData.EMPTY,
            parkingArea = ParkingAreaData.EMPTY,
            date = "",
            slot = Slot.EMPTY
        )
    }
}
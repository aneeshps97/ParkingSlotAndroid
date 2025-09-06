package com.example.parkingslot.webConnect.dto.parkingArea

import com.example.parkingslot.webConnect.dto.slot.SlotData
import com.example.parkingslot.webConnect.dto.user.UserData

data class ParkingAreaData(
    val parkingAreaId: Int,
    val name: String,
    val ticketLine1:String,
    val ticketLine2: String,
    val adminId: Int,
    val slots: List<SlotData>,
    val users: List<UserData>
) {
    companion object {
        val EMPTY = ParkingAreaData(
            parkingAreaId = 0,
            name = "",
            ticketLine1 = "",
            ticketLine2 = "",
            adminId = 0,
            slots = emptyList(),
            users = emptyList()
        )
    }
}
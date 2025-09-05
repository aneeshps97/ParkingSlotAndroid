package com.example.parkingslot.webConnect.dto.user

import com.example.parkingslot.webConnect.dto.parkingArea.ParkingAreaData

data class UserData(
    val userId: Int,
    val name: String,
    val email: String,
    val password: String,
    val userToken: String,
    val parkingAreas: List<ParkingAreaData>
) {
    companion object {
        val EMPTY = UserData(
            userId = 0,
            name = "",
            email = "",
            password = "",
            userToken = "",
            parkingAreas = emptyList()
        )
    }
}

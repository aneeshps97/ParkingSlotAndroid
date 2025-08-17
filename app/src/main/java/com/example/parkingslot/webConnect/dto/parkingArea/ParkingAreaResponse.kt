package com.example.parkingslot.webConnect.dto.parkingArea

data class ParkingAreaResponse(
    val statusCode: Int,
    val status: Int,
    val message: String,
    val data: ParkingAreaData
)
data class ParkingAreaData(
    val parkingAreaId:Int,
    val name: String,
    val adminId:Int,
    val slotData: List<SlotData>,
    val users: List<UserData>
)
data class UserData(
    val id: Int,
    val name: String,
    val email: String,
    val password: String,
    val userToken: String
)

data class SlotData(
    val slotId: Int,
    val name: String
)


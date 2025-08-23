package com.example.parkingslot.webConnect.dto.user

data class UserResponse(
    val statusCode: Int,
    val status: Int,
    val message: String,
    val data: UserData
)
data class UserData(
    val userId: Int,
    val name: String,
    val email: String,
    val password: String,
    val userToken: String,
    val parkingAreas : List<ParkingAreaData>
)

data class ParkingAreaData(
    val parkingAreaId:Int,
    val name: String,
    val adminId:Int,
    val slots: List<SlotData>,
    val users: List<Users>
)
data class Users(
    val userId: Int,
    val name: String,
    val email: String,
    val password: String,
    val userToken: String
)

data class SlotData(
    val slotId: Int,
    val name: String
)

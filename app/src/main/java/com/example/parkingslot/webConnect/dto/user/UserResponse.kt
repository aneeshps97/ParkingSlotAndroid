package com.example.parkingslot.webConnect.dto.user
data class UserResponse(
    val statusCode: Int,
    val status: Int,
    val message: String,
    val data: UserData
)
package com.example.parkingslot.webConnect.dto.signup

data class SignUpResponse(
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
    val userToken: String
)

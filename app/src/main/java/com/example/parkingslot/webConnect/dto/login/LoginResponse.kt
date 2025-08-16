package com.example.parkingslot.webConnect.dto.login

data class LoginResponse(
    val statusCode: Int,
    val status: Int,
    val message: String,
    val data: UserData
)
data class UserData(
    val id: Int,
    val name: String,
    val email: String,
    val password: String,
    val userToken: String
)

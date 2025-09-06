package com.example.parkingslot.webConnect.dto.login

import com.example.parkingslot.webConnect.dto.user.UserData

data class LoginResponse(
    val statusCode: Int,
    val status: Int,
    val message: String,
    val data: UserData
)

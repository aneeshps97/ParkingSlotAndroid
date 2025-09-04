package com.example.parkingslot.webConnect.dto.signup

import com.example.parkingslot.webConnect.dto.user.UserData

data class SignUpResponse(
    val statusCode: Int,
    val status: Int,
    val message: String,
    val data: UserData
)

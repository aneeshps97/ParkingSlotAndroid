package com.example.parkingslot.webConnect.requestresponse

import android.R

data class LoginResponse(
    val id: Int,
    val name: String,
    val email: String,
    val password: String,
    val userToken: String
)

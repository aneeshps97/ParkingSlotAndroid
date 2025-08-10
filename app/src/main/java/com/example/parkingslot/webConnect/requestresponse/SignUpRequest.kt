package com.example.parkingslot.webConnect.requestresponse

data class SignUpRequest(
    val name: String,
    val email: String,
    val password: String
)
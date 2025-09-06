package com.example.parkingslot.webConnect.dto.slot


data class SlotDataResponse(
    val statusCode: Int,
    val status: Int,
    val message: String,
    val data: SlotData
)

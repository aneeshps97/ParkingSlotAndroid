package com.example.parkingslot.webConnect.dto.slot

import com.example.parkingslot.webConnect.dto.parkingArea.ParkingAreaData

data class SlotDataResponse(
    val statusCode: Int,
    val status: Int,
    val message: String,
    val data: SlotData
)

package com.example.parkingslot.webConnect.dto.parkingArea


data class ParkingAreaResponse(
    val statusCode: Int,
    val status: Int,
    val message: String,
    val data: ParkingAreaData
) {
    companion object {
        val EMPTY = ParkingAreaResponse(
            statusCode = 0,
            status = 0,
            message = "",
            data = ParkingAreaData.EMPTY
        )
    }
}


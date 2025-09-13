package com.example.parkingslot.webConnect.dto.booking

data class AutoAssignRequest(
    val parkingAreaId: Int,
    val userIds: List<Int>,
    val slotIds: List<Int>,
    val startDate: String,
    val endDate: String,
    val frequency :Int
)
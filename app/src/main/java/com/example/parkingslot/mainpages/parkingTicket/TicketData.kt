package com.example.parkingslot.mainpages.parkingTicket

import java.sql.Date

data class TicketData(
    val ticketNumber: String,
    val date: String,
    val parkingAreaName: String,
    val userName: String,
    val authKey: String
)

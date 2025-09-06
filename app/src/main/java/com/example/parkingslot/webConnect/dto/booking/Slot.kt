package com.example.parkingslot.webConnect.dto.booking

data class Slot(
    val slotId: Int,
    val name: String
) {
    companion object {
        val EMPTY = Slot(
            slotId = 0,
            name = ""
        )
    }
}
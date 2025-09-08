package com.example.parkingslot.webConnect.dto.user

data class User(
    val id: Int?,
    val name: String
) {
    companion object {
        val EMPTY = User(0, "")
    }
}

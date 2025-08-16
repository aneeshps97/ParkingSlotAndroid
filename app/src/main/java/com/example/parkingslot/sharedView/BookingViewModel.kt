package com.example.parkingslot.sharedView

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.example.parkingslot.webConnect.dto.booking.BookingResponse

class BookingViewModel : ViewModel() {
    var slotsData by mutableStateOf<List<BookingResponse>>(emptyList())
}

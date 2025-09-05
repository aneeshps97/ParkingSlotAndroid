package com.example.parkingslot.webConnect.repository

import com.example.parkingslot.webConnect.dto.booking.BookingData
import com.example.parkingslot.webConnect.dto.booking.BookingResponse
import com.example.parkingslot.webConnect.retrofit.ParkingSlotApi
import com.example.parkingslot.webConnect.retrofit.RetrofitService
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class BookingRepository {

    fun getBookingByUserParkingAndDate(
        userId: Int,
        parkingAreaId: Int,
        date: String,
        callback: (Result<BookingResponse>) -> Unit
    ) {
        val api = RetrofitService.getRetrofit().create(ParkingSlotApi::class.java)

        api.getBookingByUserParkingAndDate(userId, parkingAreaId, date)
            .enqueue(object : Callback<BookingResponse> {
                override fun onResponse(
                    call: Call<BookingResponse>,
                    response: Response<BookingResponse>
                ) {
                    val body = response.body()
                    if (response.isSuccessful && body != null) {
                        callback(Result.success(body))
                    } else {
                        callback(Result.failure(Exception("No booking found for given date")))
                    }
                }

                override fun onFailure(call: Call<BookingResponse>, t: Throwable) {
                    callback(Result.failure(t))
                }
            })
    }


    fun getCurrentBookingOfUserRepository(
        userId: Int,
        parkingAreaId: Int,
        callback: (Result<List<BookingData>>) -> Unit
    ) {
        val api = RetrofitService.getRetrofit().create(ParkingSlotApi::class.java)

        api.getBookingByUserForParkingArea(userId, parkingAreaId)
            .enqueue(object : Callback<BookingResponse> {
                override fun onResponse(
                    call: Call<BookingResponse>,
                    response: Response<BookingResponse>
                ) {
                    val body = response.body()
                    if (response.isSuccessful && body != null && body.status == 0) {
                        callback(Result.success(body.data ?: emptyList()))
                    } else {
                        callback(Result.failure(Exception(body?.message ?: "No booked slots")))
                    }
                }

                override fun onFailure(call: Call<BookingResponse>, t: Throwable) {
                    callback(Result.failure(t))
                }
            })
    }


    fun getCurrentBookingByParkingArea(
        parkingAreaId: Int,
        callback: (Result<List<BookingData>>) -> Unit
    ) {
        val api = RetrofitService.getRetrofit().create(ParkingSlotApi::class.java)

        api.getBookingByParkingArea(parkingAreaId)
            .enqueue(object : Callback<BookingResponse> {
                override fun onResponse(
                    call: Call<BookingResponse>,
                    response: Response<BookingResponse>
                ) {
                    val body = response.body()
                    if (response.isSuccessful && body != null && body.status == 0) {
                        callback(Result.success(body.data ?: emptyList()))
                    } else {
                        callback(Result.failure(Exception(body?.message ?: "No bookings found")))
                    }
                }

                override fun onFailure(call: Call<BookingResponse>, t: Throwable) {
                    callback(Result.failure(t))
                }
            })
    }


}
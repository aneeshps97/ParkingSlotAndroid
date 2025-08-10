package com.example.parkingslot.webConnect.retrofit

import com.example.parkingslot.webConnect.requestresponse.LoginRequest
import com.example.parkingslot.webConnect.requestresponse.LoginResponse
import com.example.parkingslot.webConnect.requestresponse.SignUpRequest
import com.example.parkingslot.webConnect.requestresponse.BookingResponse
import retrofit2.http.POST
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Query

interface ParkingSlotApi {
    @POST("/parkingslot/logIn")
    fun login(@Body request: LoginRequest): Call<LoginResponse>

    @POST(value = "parkingslot/signUp")
    fun signUp(@Body request: SignUpRequest): Call<LoginResponse>

    @GET("parkingslot/findSlot")
    fun findCurrentParkingSlot(
        @Query("userId") userId: Int,
        @Query("date") date: String // or LocalDate if using Moshi/JavaTimeAdapter
    ): Call<BookingResponse>

    @GET("parkingslot/findSlots")
    fun findSlotsByUserId(
        @Query("userId") userId: Int
    ): Call <List<BookingResponse>>

    @GET("parkingslot/freeSlots")
    fun findFreeSlots(
    ): Call <List<BookingResponse>>

    @GET("parkingslot/releaseSlot")
    fun releaseSlot(
        @Query("slotId") slotId: Int?
    ): Call <BookingResponse>

    @GET("parkingslot/bookFreeSlot")
    fun bookFreeSlot(
        @Query("slotId") slotId: Int?,
        @Query("userId") userId: Int?
    ): Call <BookingResponse>
}
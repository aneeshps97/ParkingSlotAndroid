package com.example.parkingslot.webConnect.retrofit

import com.example.parkingslot.webConnect.dto.parkinglot.AssignSlotsToUserRequest
import com.example.parkingslot.webConnect.dto.parkinglot.AssignSlotsToUserResponse
import com.example.parkingslot.webConnect.dto.login.LoginRequest
import com.example.parkingslot.webConnect.dto.login.LoginResponse
import com.example.parkingslot.webConnect.dto.signup.SignUpRequest
import com.example.parkingslot.webConnect.dto.booking.BookingResponse
import com.example.parkingslot.webConnect.dto.parkinglot.ParkingAreaRequest
import com.example.parkingslot.webConnect.dto.parkinglot.ParkingAreaResponse
import com.example.parkingslot.webConnect.dto.parkinglot.ParkingAreaSlotRequest
import com.example.parkingslot.webConnect.dto.parkinglot.ParkingAreaSlotResponse
import com.example.parkingslot.webConnect.dto.parkinglot.ParkingAreaUserRequest
import com.example.parkingslot.webConnect.dto.parkinglot.ParkingAreaUserResponse
import com.example.parkingslot.webConnect.dto.signup.SignUpResponse
import retrofit2.http.POST
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Query

interface ParkingSlotApi {
    @POST("/parkingslot/logIn")
    fun login(@Body request: LoginRequest): Call<LoginResponse>

    @POST(value = "parkingslot/signUp")
    fun signUp(@Body request: SignUpRequest): Call<SignUpResponse>

    @GET("parkingslot/findSlot")
    fun findCurrentParkingSlot(
        @Query("userId") userId: Int,
        @Query(value = "pId") pId:Int,
        @Query("date") date: String // or LocalDate if using Moshi/JavaTimeAdapter
    ): Call<BookingResponse>

    @GET("parkingslot/findSlots")
    fun findSlotsByUserIdAndPId(
        @Query("userId") userId: Int,
        @Query(value = "pId") pId:Int,
    ): Call <List<BookingResponse>>

    @GET("parkingslot/freeSlots")
    fun findFreeSlots(
        @Query(value = "pId") pId:Int
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

    @POST(value = "parkingslot/createParkingArea")
    fun createParkingArea(@Body request: ParkingAreaRequest): Call<ParkingAreaResponse>

    @POST(value = "parkingslot/addSlotsToParkingArea")
    fun addSlotsToParkingArea(@Body request: ParkingAreaSlotRequest):Call<ParkingAreaSlotResponse>

    @POST(value = "parkingslot/addUserToParkingArea")
    fun addUsersToParkingArea(@Body request: ParkingAreaUserRequest):Call<ParkingAreaUserResponse>

    @POST(value = "/parkingslot/assignSlotsToUser")
    fun assignSlotsToUser(@Body request: AssignSlotsToUserRequest):Call<AssignSlotsToUserResponse>

    @GET("parkingslot/findParkingAreaAssignedToUser")
    fun findParkingAreaByUser(
        @Query("userId") userId: Int?
    ): Call <List<ParkingAreaResponse>>


}
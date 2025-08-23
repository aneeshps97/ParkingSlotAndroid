package com.example.parkingslot.webConnect.retrofit

import com.example.parkingslot.webConnect.dto.booking.BookingRequest
import com.example.parkingslot.webConnect.dto.booking.BookingResponse
import com.example.parkingslot.webConnect.dto.login.LoginRequest
import com.example.parkingslot.webConnect.dto.login.LoginResponse
import com.example.parkingslot.webConnect.dto.parkingArea.ParkingAreaRequest
import com.example.parkingslot.webConnect.dto.parkingArea.ParkingAreaResponse
import com.example.parkingslot.webConnect.dto.parkingArea.Slot
import com.example.parkingslot.webConnect.dto.signup.SignUpRequest
import com.example.parkingslot.webConnect.dto.signup.SignUpResponse
import com.example.parkingslot.webConnect.dto.user.User
import com.example.parkingslot.webConnect.dto.user.UserResponse
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
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

    @POST(value = "parkingslot/parkingArea/createParkingArea")
    fun createParkingArea(@Body request: ParkingAreaRequest): Call<ParkingAreaResponse>

    @PUT("parkingslot/parkingArea/{id}/addSlots")
    fun addSlotsToParkingArea(
        @Path("id") id: Int, @Body slots: List<Slot>
    ): Call<ParkingAreaResponse>

    @GET("parkingslot/findUserByEmail")
    fun findUserByEmail (@Query("email") email: String?):Call<UserResponse>

    @PUT("parkingslot/parkingArea/{id}/addUsers")
    fun addUsersToParkingArea(
        @Path("id") id: Int, @Body slots: List<Int>
    ): Call<ParkingAreaResponse>


    @POST(value = "/parkingslot/booking/assignSlotsToUser")
    fun assignSlotsToUser(@Body request: BookingRequest):Call<BookingResponse>


    @GET("parkingslot/parkingArea/viewParkingArea")
    fun findParkingAreaById(
        @Query("parkingAreaId") parkingAreaId: Int?
    ): Call <ParkingAreaResponse>

    @GET("parkingslot/findUserById")
    fun findUserDetailsById(
        @Query("userId") userId: Int?
    ): Call<UserResponse>



}
package com.example.parkingslot.webConnect.retrofit

import com.example.parkingslot.webConnect.dto.booking.BookingRequest
import com.example.parkingslot.webConnect.dto.booking.BookingResponse
import com.example.parkingslot.webConnect.dto.login.LoginRequest
import com.example.parkingslot.webConnect.dto.login.LoginResponse
import com.example.parkingslot.webConnect.dto.parkingArea.ParkingAreaRequest
import com.example.parkingslot.webConnect.dto.parkingArea.ParkingAreaResponse
import com.example.parkingslot.webConnect.dto.slot.Slot
import com.example.parkingslot.webConnect.dto.signup.SignUpRequest
import com.example.parkingslot.webConnect.dto.signup.SignUpResponse
import com.example.parkingslot.webConnect.dto.slot.SlotData
import com.example.parkingslot.webConnect.dto.slot.SlotDataResponse
import com.example.parkingslot.webConnect.dto.user.UserResponse
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.DELETE
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

    @GET("parkingslot/booking/by-date")
    fun getBookingByUserParkingAndDate(
        @Query("userId") userId: Int,
        @Query(value = "parkingAreaId") pId:Int,
        @Query("date") date: String // or LocalDate if using Moshi/JavaTimeAdapter
    ): Call<BookingResponse>

    @GET("parkingslot/booking/by-user")
    fun getBookingByUserForParkingArea(
        @Query("userId") userId: Int,
        @Query(value = "parkingAreaId") pId:Int,
    ): Call <BookingResponse>

    @GET("parkingslot/booking/parkingArea")
    fun getBookingByParkingArea(
        @Query(value = "parkingAreaId") parkingAreaId:Int,
    ): Call <BookingResponse>

    @GET("parkingslot/booking/getFreeSlots")
    fun getFreeSlotsInParkingArea(
        @Query(value = "parkingAreaId") parkingAreaId:Int
    ): Call <BookingResponse>

    @PUT("parkingslot/booking/release")
    fun releaseSlot(
        @Query("bookingId") bookingId: Int?
    ): Call <BookingResponse>

    @PUT("parkingslot/booking/bookSlotForUser")
    fun bookSlotForUser(
        @Query("userId") userId: Int?,
        @Query("bookingId") bookingId: Int?
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
        @Path("id") id: Int, @Body users: List<Int>
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

    @PUT("/parkingslot/parkingArea/updateName/{id}")
    fun updateParkingAreaName(
        @Path("id") id: Int,
        @Query("newName") newName: String
    ): Call<ParkingAreaResponse>

    @DELETE("/parkingslot/slot/deleteSlot/{slotId}")
    fun deleteSlot(
        @Path("slotId") slotId: Int,
    ): Call<SlotDataResponse>

    @DELETE("/parkingslot/booking/removeBooking")
    fun removeBooking(
        @Query("bookingId") bookingId: Int,
    ): Call<BookingResponse>

    @PUT("/parkingslot/parkingArea/removeUserFromParkingArea/{parkingAreaId}")
    fun removeUserFromParkingArea(
        @Path("parkingAreaId") parkingAreaId: Int,
        @Query("userId") userId:Int
    ): Call<ParkingAreaResponse>

    @PUT("/parkingslot/slot/updateSlot/{slotId}")
    fun updateSlot(
        @Body slot: SlotData,
        @Path("slotId") slotId: Int

    ): Call<SlotDataResponse>

    @POST("/parkingslot/slot/createSlot/{parkingAreaId}")
    fun addSlots(
        @Body slot: Slot,
        @Path(value = "parkingAreaId") parkingAreaId: Int

    ): Call<SlotDataResponse>

    @DELETE("parkingslot/parkingArea/deleteParkingArea/{parkingAreaId}")
    fun deleteParkingArea(
        @Path("parkingAreaId") id: Int
    ): Call<ParkingAreaResponse>



}
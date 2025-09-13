package com.example.parkingslot.webConnect.repository

import android.content.Context
import android.widget.Toast
import androidx.navigation.NavController
import com.example.parkingslot.Route.Routes
import com.example.parkingslot.webConnect.dto.booking.BookingResponse
import com.example.parkingslot.webConnect.dto.parkingArea.ParkingAreaResponse
import com.example.parkingslot.webConnect.dto.slot.Slot
import com.example.parkingslot.webConnect.dto.user.UserData
import com.example.parkingslot.webConnect.retrofit.ParkingSlotApi
import com.example.parkingslot.webConnect.retrofit.RetrofitService
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ParkingAreaRepository {

    fun addSlotsToParkingArea(
        parkingAreaId: String?,
        listOfSlots: List<String>,
        callback: (Result<ParkingAreaResponse>) -> Unit
    ) {
        val api = RetrofitService.getRetrofit().create(ParkingSlotApi::class.java)
        val request = createSlotObjectFromTheData(listOfSlots)

        val id = parkingAreaId?.toIntOrNull()
        if (id == null) {
            callback(Result.failure(IllegalArgumentException("Invalid parkingAreaId")))
            return
        }

        api.addSlotsToParkingArea(id, request).enqueue(object : Callback<ParkingAreaResponse> {
            override fun onResponse(
                call: Call<ParkingAreaResponse>,
                response: Response<ParkingAreaResponse>
            ) {
                val body = response.body()
                if (body != null && body.status == 0) {
                    callback(Result.success(body))
                } else {
                    callback(Result.failure(Exception(body?.message ?: "Unknown error")))
                }
            }

            override fun onFailure(call: Call<ParkingAreaResponse>, t: Throwable) {
                callback(Result.failure(t))
            }
        })
    }

    private fun createSlotObjectFromTheData(slotList: List<String>): MutableList<Slot> {
        return slotList.map { Slot(name = it) }.toMutableList()
    }

    fun findParkingAreaById(
        parkingAreaId: String,
        callback: (Result<ParkingAreaResponse>) -> Unit
    ) {
        val api = RetrofitService.getRetrofit().create(ParkingSlotApi::class.java)

        api.findParkingAreaById(parkingAreaId.toInt())
            .enqueue(object : Callback<ParkingAreaResponse> {
                override fun onResponse(
                    call: Call<ParkingAreaResponse>,
                    response: Response<ParkingAreaResponse>
                ) {
                    val body = response.body()
                    if (response.isSuccessful && body != null) {
                        if (body.status == 0) {
                            callback(Result.success(body))
                        } else {
                            callback(Result.failure(Exception(body.message)))
                        }
                    } else {
                        callback(Result.failure(Exception(body?.message)))
                    }
                }

                override fun onFailure(call: Call<ParkingAreaResponse>, t: Throwable) {
                    callback(Result.failure(t))
                }
            })
    }


    fun updateParkingAreaBasicDetails(
        parkingAreaId: Int,
        parkingAreaName: String,
        ticketLine1: String,
        ticketLine2: String,
        callback: (Result<ParkingAreaResponse>) -> Unit
    ) {
        val api = RetrofitService.getRetrofit().create(ParkingSlotApi::class.java)

        api.updateParkingAreaBasicDetails(
            id = parkingAreaId,
            newName = parkingAreaName,
            newTicketLine1 = ticketLine1,
            newTicketLine2 = ticketLine2
        )
            .enqueue(object : Callback<ParkingAreaResponse> {
                override fun onResponse(
                    call: Call<ParkingAreaResponse>,
                    response: Response<ParkingAreaResponse>
                ) {
                    val body = response.body()
                    if (response.isSuccessful && body != null && body.status == 0) {
                        callback(Result.success(body))
                    } else {
                        val errorMsg = response.body()?.message
                        callback(Result.failure(Exception(errorMsg)))
                    }
                }

                override fun onFailure(call: Call<ParkingAreaResponse>, t: Throwable) {
                    callback(Result.failure(t))
                }
            })
    }


    fun getFreeSlotsInParkingArea(
        parkingAreaId: Int,
        callback: (Result<BookingResponse>) -> Unit
    ) {
        val api = RetrofitService.getRetrofit().create(ParkingSlotApi::class.java)

        api.getFreeSlotsInParkingArea(parkingAreaId)
            .enqueue(object : Callback<BookingResponse> {
                override fun onResponse(
                    call: Call<BookingResponse>,
                    response: Response<BookingResponse>
                ) {
                    val body = response.body()
                    if (response.isSuccessful && body != null && body.status == 0) {
                        callback(Result.success(body))
                    } else {
                        val errorMsg = response.body()?.message
                        callback(
                            Result.failure(
                                Exception(
                                    errorMsg
                                )
                            )
                        )
                    }
                }

                override fun onFailure(call: Call<BookingResponse>, t: Throwable) {
                    callback(Result.failure(t))
                }
            })
    }


    fun releaseSlotRepository(
        bookingId: Int,
        callback: (Result<BookingResponse>) -> Unit
    ) {
        val api = RetrofitService.getRetrofit().create(ParkingSlotApi::class.java)
        api.releaseSlot(bookingId)
            .enqueue(object : Callback<BookingResponse> {
                override fun onResponse(
                    call: Call<BookingResponse>,
                    response: Response<BookingResponse>
                ) {
                    val body = response.body()
                    if (response.isSuccessful && body != null) {
                        callback(Result.success(body))
                    } else {
                        val errorMsg = response.body()?.message
                        callback(Result.failure(Exception(errorMsg)))
                    }
                }

                override fun onFailure(call: Call<BookingResponse>, t: Throwable) {
                    callback(Result.failure(t))
                }
            })
    }


    fun bookSlotForUser(
        userId: Int,
        bookingId: Int,
        callback: (Result<BookingResponse>) -> Unit
    ) {
        val api = RetrofitService.getRetrofit().create(ParkingSlotApi::class.java)
        api.bookSlotForUser(userId = userId, bookingId = bookingId)
            .enqueue(object : Callback<BookingResponse> {
                override fun onResponse(
                    call: Call<BookingResponse>,
                    response: Response<BookingResponse>
                ) {
                    val body = response.body()
                    if (response.isSuccessful && body != null) {
                        if (body.status == 0) {
                            callback(Result.success(body))
                        } else {
                            val errorMsg = response.body()?.message
                            callback(
                                Result.failure(
                                    Exception(
                                        errorMsg
                                    )
                                )
                            )
                        }
                    } else {
                        callback(Result.failure(Exception("Invalid response from server")))
                    }
                }

                override fun onFailure(call: Call<BookingResponse>, t: Throwable) {
                    callback(Result.failure(t))
                }
            })
    }


    fun addUserToParkingArea(
        context: Context,
        parkingAreaId: Int,
        userId: Int,
        onSuccess: (List<UserData>) -> Unit,
        onError: (String) -> Unit
    ) {
        val listOfUserIds = mutableListOf<Int>()
        listOfUserIds.add(userId)

        val api = RetrofitService.getRetrofit().create(ParkingSlotApi::class.java)
        api.addUsersToParkingArea(parkingAreaId, listOfUserIds)
            .enqueue(object : Callback<ParkingAreaResponse> {
                override fun onResponse(
                    call: Call<ParkingAreaResponse>,
                    response: Response<ParkingAreaResponse>
                ) {
                    val body = response.body()
                    if (response.isSuccessful && body != null && body.status == 0) {
                        Toast.makeText(context, "User Added", Toast.LENGTH_SHORT).show()
                        val updatedUsers = body.data?.users ?: emptyList()
                        onSuccess(updatedUsers)
                    } else {
                        val errorMsg = body?.message ?: "Unknown error"
                        Toast.makeText(context, errorMsg, Toast.LENGTH_SHORT).show()
                        onError(errorMsg)
                    }
                }

                override fun onFailure(call: Call<ParkingAreaResponse>, t: Throwable) {
                    val errorMsg = "Error: ${t.message}"
                    Toast.makeText(context, errorMsg, Toast.LENGTH_SHORT).show()
                    onError(errorMsg)
                }
            })
    }


    fun removeUserFromParkingArea(
        context: Context,
        parkingAreaId: Int,
        userId: Int,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        val api = RetrofitService.getRetrofit().create(ParkingSlotApi::class.java)
        api.removeUserFromParkingArea(parkingAreaId, userId)
            .enqueue(object : Callback<ParkingAreaResponse> {
                override fun onResponse(
                    call: Call<ParkingAreaResponse>,
                    response: Response<ParkingAreaResponse>
                ) {
                    val body = response.body()
                    if (response.isSuccessful && body?.status == 0) {
                        Toast.makeText(context, "User removed", Toast.LENGTH_SHORT).show()
                        onSuccess()
                    } else {
                        val errorMsg = body?.message ?: "Failed to remove user"
                        Toast.makeText(context, errorMsg, Toast.LENGTH_SHORT).show()
                        onError(errorMsg)
                    }
                }

                override fun onFailure(call: Call<ParkingAreaResponse>, t: Throwable) {
                    val errorMsg = "Error: ${t.message}"
                    Toast.makeText(context, errorMsg, Toast.LENGTH_SHORT).show()
                    onError(errorMsg)
                }
            })
    }


    fun changeAdmin(
        context: Context,
        parkingAreaId: Int,
        newAdminId: Int,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        val api = RetrofitService.getRetrofit().create(ParkingSlotApi::class.java)
        api.changeAdmin(parkingAreaId, newAdminId)
            .enqueue(object : Callback<ParkingAreaResponse> {
                override fun onResponse(
                    call: Call<ParkingAreaResponse>,
                    response: Response<ParkingAreaResponse>
                ) {
                    val body = response.body()
                    if (response.isSuccessful && body?.status == 0) {
                        Toast.makeText(context, response.body()?.message, Toast.LENGTH_SHORT).show()
                        onSuccess()
                    } else {
                        val errorMsg = body?.message ?: "Failed to change Admin"
                        Toast.makeText(context, errorMsg, Toast.LENGTH_SHORT).show()
                        onError(errorMsg)
                    }
                }

                override fun onFailure(call: Call<ParkingAreaResponse>, t: Throwable) {
                    val errorMsg = "Error: ${t.message}"
                    Toast.makeText(context, errorMsg, Toast.LENGTH_SHORT).show()
                    onError(errorMsg)
                }
            })
    }


}

package com.example.parkingslot.webConnect.repository

import android.content.Context
import android.content.SharedPreferences
import android.widget.Toast
import androidx.navigation.NavController
import com.example.parkingslot.Route.Routes
import com.example.parkingslot.webConnect.dto.login.LoginRequest
import com.example.parkingslot.webConnect.dto.login.LoginResponse
import com.example.parkingslot.webConnect.dto.signup.SignUpRequest
import com.example.parkingslot.webConnect.dto.signup.SignUpResponse
import com.example.parkingslot.webConnect.dto.user.UserResponse
import com.example.parkingslot.webConnect.retrofit.ParkingSlotApi
import com.example.parkingslot.webConnect.retrofit.RetrofitService
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Response
import retrofit2.Callback


class UserRepository() {
    fun login(email: String, password: String, callback: (Result<LoginResponse>) -> Unit) {
        val api = RetrofitService.getRetrofit().create(ParkingSlotApi::class.java)
        val loginRequest = LoginRequest(email = email, password = password)
        api.login(loginRequest).enqueue(object : Callback<LoginResponse> {
            override fun onResponse(call: Call<LoginResponse>, response: Response<LoginResponse>) {
                val body = response.body()
                if (response.isSuccessful && body?.status == 0) {
                    callback(Result.success(body))
                } else {
                    val errorMsg = response.errorBody()?.string()?.let {
                        JSONObject(it).getString("message")
                    } ?: "Login failed"
                    callback(Result.failure(Exception(errorMsg)))
                }
            }

            override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                callback(Result.failure(t))
            }
        })
    }


    fun signUp(
        name: String,
        email: String,
        password: String,
        callback: (Result<SignUpResponse>) -> Unit
    ) {
        val api = RetrofitService.getRetrofit().create(ParkingSlotApi::class.java)
        val request = SignUpRequest(name = name, email = email, password = password)
        api.signUp(request).enqueue(object : Callback<SignUpResponse> {
            override fun onResponse(
                call: Call<SignUpResponse>,
                response: Response<SignUpResponse>
            ) {
                val body = response.body()
                if (response.isSuccessful && body?.status == 0 && body.data != null) {
                    callback(Result.success(body))
                } else {
                    val errorMsg = response.errorBody()?.string()?.let {
                        JSONObject(it).getString("message")
                    } ?: "Signup failed"
                    callback(Result.failure(Exception(errorMsg)))
                }
            }

            override fun onFailure(call: Call<SignUpResponse>, t: Throwable) {
                callback(Result.failure(t))
            }
        })
    }




    fun findUserByEmail(
        email: String,
        callback: (Result<UserResponse>) -> Unit
    ) {
        val api = RetrofitService.getRetrofit().create(ParkingSlotApi::class.java)

        api.findUserByEmail(email)
            .enqueue(object : Callback<UserResponse> {
                override fun onResponse(
                    call: Call<UserResponse>,
                    response: Response<UserResponse>
                ) {
                    val body = response.body()
                    if (response.isSuccessful && body != null) {
                        if (body.status == 0) {
                            callback(Result.success(body))
                        } else {
                            callback(Result.failure(Exception(body.message)))
                        }
                    } else {
                        callback(Result.failure(Exception("User not found")))
                    }
                }

                override fun onFailure(call: Call<UserResponse>, t: Throwable) {
                    callback(Result.failure(t))
                }
            })
    }

    fun findUserDetailsByIdRepository(
        userId: Int,
        callback: (Result<UserResponse>) -> Unit
    ) {
        val api = RetrofitService.getRetrofit().create(ParkingSlotApi::class.java)
        val call: Call<UserResponse> = api.findUserDetailsById(userId)

        call.enqueue(object : Callback<UserResponse> {
            override fun onResponse(
                call: Call<UserResponse>,
                response: Response<UserResponse>
            ) {
                val body = response.body()
                if (body != null && body.status == 0) {
                    callback(Result.success(body))
                } else {
                    callback(Result.failure(Exception(body?.message ?: "No parking area assigned")))
                }
            }

            override fun onFailure(call: Call<UserResponse>, t: Throwable) {
                callback(Result.failure(t))
            }
        })
    }



}

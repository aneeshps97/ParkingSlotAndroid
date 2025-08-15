package com.example.parkingslot.webConnect.retrofit

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitService {
    private val retrofit: Retrofit = Retrofit.Builder()
        .baseUrl("http://192.168.30.5:8080/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    fun getRetrofit(): Retrofit = retrofit
}


package com.example.parkingslot.webConnect.retrofit

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitService {
    private val retrofit: Retrofit = Retrofit.Builder()
        .baseUrl("https://parkingslot-p94v.onrender.com/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    fun getRetrofit(): Retrofit = retrofit
}


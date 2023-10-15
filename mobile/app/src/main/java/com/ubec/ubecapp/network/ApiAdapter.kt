package com.ubec.ubecapp.network

import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

//.baseUrl("http://ubec.fortressenterprise.com.ng/api/mobile/")
//.baseUrl("http://192.168.48.49/api/mobile/")
//.baseUrl("http://ubeccms.com/api/mobile/")
//Adapter for Api calls
object ApiAdapter {
    val apiClient: ApiInterface = Retrofit.Builder()
        .baseUrl("http://ubeccms.com/api/mobile/")
        .client(OkHttpClient())
        .addConverterFactory(GsonConverterFactory.create())
        .build()
        .create(ApiInterface::class.java)
}
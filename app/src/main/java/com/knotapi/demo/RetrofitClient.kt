package com.knotapi.demo

import okhttp3.Interceptor
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class RetrofitClient private constructor() {
    val myApi: Api
    var BASE_URL = "https://sample.knotapi.com/api/"

    var client = OkHttpClient.Builder()
        .addInterceptor(Interceptor { chain: Interceptor.Chain ->
            val newRequest = chain.request().newBuilder()
                .addHeader("Environment", "sandbox")
                .addHeader("Client-Id", "ab86955e-22f4-49c3-97d7-369973f4cb9e")
                .addHeader("Client-Secret", "d1a5cde831464cd3840ccf762f63ceb7")
                .build()
            chain.proceed(newRequest)
        }).build()

    companion object {
        var instance: RetrofitClient? = null
            get() {
                if (field == null) {
                    field = RetrofitClient()
                }
                return field
            }
            private set
    }

    init {
        val retrofit: Retrofit = Retrofit.Builder()
            .client(client)
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        myApi = retrofit.create(Api::class.java)
    }
}
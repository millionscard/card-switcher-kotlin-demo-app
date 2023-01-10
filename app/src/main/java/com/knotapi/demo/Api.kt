package com.knotapi.demo

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST

interface Api {
    @POST("register")
    fun createUserAPI(@Body createUserRequest: CreateUserRequest?): Call<CreateUserResponse>

    @POST("knot/session")
    fun createSessionAPI(@Header("Authorization") token: String?, @Body createSession: CreateSession?): Call<CreateSessionResponse>
}
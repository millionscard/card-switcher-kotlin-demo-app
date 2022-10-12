package com.knotapi.demo

import com.google.gson.annotations.SerializedName

class CreateUserResponse {
    @SerializedName("token")
    var token: String? = null
}
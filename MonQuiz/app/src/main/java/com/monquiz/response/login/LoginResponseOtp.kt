package com.monquiz.response.login


import com.google.gson.annotations.SerializedName

data class LoginResponseOtp(
    @SerializedName("message")
    var message: String,
    @SerializedName("responseData")
    var responseData: ResponseData,
    @SerializedName("status")
    var status: Int
)
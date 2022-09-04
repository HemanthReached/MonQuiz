package com.monquiz.response.verifyotp_packageresponse


import com.google.gson.annotations.SerializedName

data class VerifyOtp_Response(
    @SerializedName("message")
    var message: String? = null,
    @SerializedName("responseData")
    var responseData: ResponseData? = null,
    @SerializedName("status")
    var status: Int? = null,
    @SerializedName("token")
    var token: String? = null
)
package com.monquiz.model.verifyotpmodel


import com.google.gson.annotations.SerializedName

data class VerifyOTP_Data(
    @SerializedName("countryCode")
    var countryCode: String,
    @SerializedName("mobileNumber")
    var mobileNumber: String,
    @SerializedName("otp")
    var otp: String
)
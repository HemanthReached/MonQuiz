package com.monquiz.model.inputdata


import com.google.gson.annotations.SerializedName

data class LoginOTpSend_Data(
    @SerializedName("countryCode")
    var countryCode: String,
    @SerializedName("mobileNumber")
    var mobileNumber: String,
    @SerializedName("deviceId")
    var deviceId: String,
    @SerializedName("referralCode")
    var referralCode: String

)
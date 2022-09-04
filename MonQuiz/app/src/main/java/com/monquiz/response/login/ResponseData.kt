package com.monquiz.response.login


import com.google.gson.annotations.SerializedName

data class ResponseData(
    @SerializedName("countryCode")
    var countryCode: String,
    @SerializedName("createdAt")
    var createdAt: String,
    @SerializedName("dateOfBirth")
    var dateOfBirth: String,
    @SerializedName("email")
    var email: String,
    @SerializedName("firstName")
    var firstName: String,
    @SerializedName("gender")
    var gender: String,
    @SerializedName("_id")
    var id: String,
    @SerializedName("isOTPVerified")
    var isOTPVerified: Boolean,
    @SerializedName("lastName")
    var lastName: String,
    @SerializedName("middleName")
    var middleName: String,
    @SerializedName("mobileNumber")
    var mobileNumber: String,
    @SerializedName("otp")
    var otp: String,
    @SerializedName("photo")
    var photo: String,
    @SerializedName("status")
    var status: Boolean,
    @SerializedName("updatedAt")
    var updatedAt: String,
    @SerializedName("userName")
    var userName: String,
    @SerializedName("__v")
    var v: Int,
    @SerializedName("referralCode")
    var referralCode: String
    , @SerializedName("isReferredUser")
    var isReferredUser: Boolean

)
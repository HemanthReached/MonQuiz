package com.monquiz.response.verifyotp_packageresponse


import com.google.gson.annotations.SerializedName

data class ResponseData(
    @SerializedName("countryCode")
    var countryCode: String? = null,
    @SerializedName("dateOfBirth")
    var dateOfBirth: String? = null,
    @SerializedName("email")
    var email: String? = null,
    @SerializedName("firstName")
    var firstName: String? = null,
    @SerializedName("gender")
    var gender: String? = null,
    @SerializedName("_id")
    var id: String? = null,
    @SerializedName("lastName")
    var lastName: String? = null,
    @SerializedName("middleName")
    var middleName: String? = null,
    @SerializedName("photo")
    var photo: String? = null,
    @SerializedName("status")
    var status: Boolean? = null,
    @SerializedName("userId")
    var userId: String? = null,
    @SerializedName("userName")
    var userName: String? = null
)
package com.monquiz.model.inputdata.updateprofile


import com.google.gson.annotations.SerializedName

data class UpdateProfileInputBody(
    @SerializedName("dateOfBirth")
    var dateOfBirth: String,
    @SerializedName("firstName")
    var firstName: String,
    @SerializedName("gender")
    var gender: String,
    @SerializedName("lastName")
    var lastName: String,
    @SerializedName("middleName")
    var middleName: String,
    @SerializedName("userName")
    var userName: String,
    @SerializedName("userId")
    var userid:String
)
package com.monquiz.model.inputdata.updateprofile


import com.google.gson.annotations.SerializedName

data class GetUserProfileInput(
    @SerializedName("userId")
    var userId: String
)
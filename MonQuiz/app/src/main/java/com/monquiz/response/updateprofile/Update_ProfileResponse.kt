package com.monquiz.response.updateprofile


import com.google.gson.annotations.SerializedName

data class Update_ProfileResponse(
    @SerializedName("message")
    var message: String? = null,
    @SerializedName("status")
    var status: Int? = null
)
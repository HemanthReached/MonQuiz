package com.monquiz.response.getdailylimit


import com.google.gson.annotations.SerializedName

data class GetDailyLimit_INput(
    @SerializedName("userId")
    var userId: String? = null
)
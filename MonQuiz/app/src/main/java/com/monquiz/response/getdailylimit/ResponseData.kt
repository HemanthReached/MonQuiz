package com.monquiz.response.getdailylimit


import com.google.gson.annotations.SerializedName

data class ResponseData(
    @SerializedName("amount")
    var amount: Int? = null,
    @SerializedName("dailyLimitId")
    var dailyLimitId: String? = null,
    @SerializedName("remainingAmount")
    var remainingAmount: Int? = null,
    @SerializedName("spentAmount")
    var spentAmount: Int? = null,
    @SerializedName("userDailyLimitId")
    var userDailyLimitId: String? = null,
    @SerializedName("userId")
    var userId: String? = null
)
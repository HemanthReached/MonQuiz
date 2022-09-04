package com.monquiz.response.wallet.res


import com.google.gson.annotations.SerializedName

data class ResponseData(
    @SerializedName("userId")
    var userId: String? = null,
    @SerializedName("walletBalance")
    var walletBalance: Double? = null,
    @SerializedName("referralBalance")
    var referralBalance : Double? = null
)
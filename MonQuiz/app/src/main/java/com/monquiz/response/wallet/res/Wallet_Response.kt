package com.monquiz.response.wallet.res


import com.google.gson.annotations.SerializedName

data class Wallet_Response(
    @SerializedName("message")
    var message: String? = null,
    @SerializedName("responseData")
    var responseData: ResponseData? = null,
    @SerializedName("status")
    var status: Int? = null
)
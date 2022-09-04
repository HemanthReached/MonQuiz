package com.monquiz.response.wallet.response


import com.google.gson.annotations.SerializedName

data class VerifyResponse(
    @SerializedName("message")
    var message: String? = null,
    @SerializedName("responseData")
    var responseData: List<ResponseData>? = null,
    @SerializedName("status")
    var status: Int? = null
)
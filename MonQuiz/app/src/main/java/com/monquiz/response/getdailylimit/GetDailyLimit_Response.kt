package com.monquiz.response.getdailylimit


import com.google.gson.annotations.SerializedName

data class GetDailyLimit_Response(
    @SerializedName("message")
    var message: String? = null,
    @SerializedName("responseData")
    var responseData: ResponseData? = null,
    @SerializedName("status")
    var status: Int? = null
)
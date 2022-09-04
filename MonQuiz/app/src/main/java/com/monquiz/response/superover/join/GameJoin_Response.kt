package com.monquiz.response.superover.join


import com.google.gson.annotations.SerializedName

data class GameJoin_Response(
    @SerializedName("code")
    var code: Int? = null,
    @SerializedName("message")
    var message: String? = null,
    @SerializedName("responseData")
    var responseData: ResponseData? = null,
    @SerializedName("status")
    var status: Int? = null
)
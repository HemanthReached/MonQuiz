package com.monquiz.response.updatecheck

import com.google.gson.annotations.SerializedName

data class UpdateResponse(
    @SerializedName("message")
    var message: String? = null,
    @SerializedName("responseData")
    var responseData: ResponseData? = null,
    @SerializedName("status")
    var status: Int? = null
)

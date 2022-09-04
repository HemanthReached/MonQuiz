package com.monquiz.response.review.response

import com.google.gson.annotations.SerializedName

data class CreateFeedBackResponse(
    @SerializedName("status")
    var status: Int? = null,
    @SerializedName("message")
    var message: String? = null,
    @SerializedName("responseData")
    var responseData: ResponseData? = null
)

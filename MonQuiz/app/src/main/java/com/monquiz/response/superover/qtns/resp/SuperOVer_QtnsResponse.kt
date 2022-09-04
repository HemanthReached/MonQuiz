package com.monquiz.response.superover.qtns.resp


import com.google.gson.annotations.SerializedName

data class SuperOVer_QtnsResponse(
    @SerializedName("message")
    var message: String? = null,
    @SerializedName("responseData")
    var responseData: ResponseData? = null,
    @SerializedName("status")
    var status: Int? = null
)
package com.monquiz.response.getstakesresp


import com.google.gson.annotations.SerializedName

data class GetAll_StakesResponse(
    @SerializedName("message")
    var message: String? = null,
    @SerializedName("responseData")
    var responseData: List<ResponseData>? = null,
    @SerializedName("status")
    var status: Int? = null
)
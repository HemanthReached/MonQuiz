package com.monquiz.response.superover.qtns.submit.resp


import com.google.gson.annotations.SerializedName

data class SuperOver_QtnSubmitResponse(
    @SerializedName("responseData")
    var responseData: Error? = null,
    @SerializedName("message")
    var message: String? = null,
    @SerializedName("status")
    var status: Int? = null
)
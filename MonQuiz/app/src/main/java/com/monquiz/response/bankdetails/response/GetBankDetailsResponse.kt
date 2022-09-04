package com.monquiz.response.bankdetails.response

import com.google.gson.annotations.SerializedName

data class GetBankDetailsResponse(
    @SerializedName("status")
    var status: Int? = null,
    @SerializedName("message")
    var message: String? = null,
    @SerializedName("responseData")
    var responseData: ResponseData? = null,
)
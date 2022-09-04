package com.monquiz.response.transactionaapi


import com.google.gson.annotations.SerializedName

data class TransactionApi_Response(
    @SerializedName("message")
    var message: String? = null,
    @SerializedName("responseData")
    var responseData: List<ResponseData>? = null,
    @SerializedName("status")
    var status: Int? = null
)
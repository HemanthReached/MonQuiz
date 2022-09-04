package com.monquiz.response.transactionaapi.input


import com.google.gson.annotations.SerializedName

data class TransactionApi_Input(
    @SerializedName("userId")
    var userId: String? = null
)
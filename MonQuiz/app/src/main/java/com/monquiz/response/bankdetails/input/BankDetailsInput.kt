package com.monquiz.response.bankdetails.input

import com.google.gson.annotations.SerializedName

data class BankDetailsInput(
    @SerializedName("userId")
    var userId: String? = null,
    @SerializedName("bankName")
    var bankName: String? = null,
    @SerializedName("accountNumber")
    var accountNumber: String? = null,
    @SerializedName("ifscCode")
    var ifscCode: String? = null
)
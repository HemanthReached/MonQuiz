package com.monquiz.response.bankdetails.response

import com.google.gson.annotations.SerializedName
data class BankDetails(
    @SerializedName("bankName")
    var bankName: String? = null,
    @SerializedName("accountNumber")
    var accountNumber: String? = null,
    @SerializedName("ifscCode")
    var ifscCode: String? = null,
    @SerializedName("_id")
    var id: String? = null)

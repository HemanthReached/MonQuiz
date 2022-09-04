package com.monquiz.response.bankdetails.response

import com.google.gson.annotations.SerializedName

data class ResponseData(
    @SerializedName("panDetails")
    var panDetails: List<PanDetails>? = null,
    @SerializedName("bankDetails")
    var bankDetails: List<BankDetails>? = null,
)

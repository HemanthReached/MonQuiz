package com.monquiz.response.wallet.response


import com.google.gson.annotations.SerializedName

data class ResponseData(
    @SerializedName("fullName")
    var fullName: String? = null,
    @SerializedName("panNumber")
    var panNumber: String? = null,
    @SerializedName("panStatus")
    var panStatus: Boolean? = null,
    @SerializedName("_id")
    var id: String? = null
)
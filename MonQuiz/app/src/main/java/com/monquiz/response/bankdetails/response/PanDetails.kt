package com.monquiz.response.bankdetails.response

import com.google.gson.annotations.SerializedName

data class PanDetails(
    @SerializedName("panNumber")
    var panNumber: String? = null,
    @SerializedName("fullName")
    var fullName: String? = null,
    @SerializedName("panStatus")
    var panStatus: Boolean? = null,
    @SerializedName("_id")
    var id: String? = null
)

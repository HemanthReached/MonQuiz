package com.monquiz.response.wallet.input

import com.google.gson.annotations.SerializedName

data class verifyInput(
    @SerializedName("userId")
    var userId: String? = null,
    @SerializedName("fullName")
    var fullName: String? = null,
    @SerializedName("panNumber")
    var panNumber: String? = null)
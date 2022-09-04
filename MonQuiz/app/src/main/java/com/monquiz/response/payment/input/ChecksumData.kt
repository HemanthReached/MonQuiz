package com.monquiz.response.leaderboard.input

import com.google.gson.annotations.SerializedName

data class ChecksumData(
    @SerializedName("userId")
    var userId: String? = null,
    @SerializedName("amount")
    var amount: String? = null,
    @SerializedName("isSignature")
    var isSignature : Boolean? = null,
    @SerializedName("transactionId")
    var transactionId: String? = null,
    @SerializedName("paytmTransactionId")
    var paytmTransactionId: String? = null,
    @SerializedName("transactionStatus")
    var transactionStatus: String? = null,)

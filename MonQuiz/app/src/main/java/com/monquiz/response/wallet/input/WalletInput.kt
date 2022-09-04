package com.monquiz.response.wallet.input


import com.google.gson.annotations.SerializedName

data class WalletInput(
    @SerializedName("userId")
    var userId: String? = null)
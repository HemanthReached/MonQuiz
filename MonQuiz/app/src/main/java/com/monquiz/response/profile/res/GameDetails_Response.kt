package com.monquiz.response.wallet.res


import com.google.gson.annotations.SerializedName
import com.monquiz.response.profile.res.GameDetails

data class GameDetails_Response(
    @SerializedName("message")
    var message: String? = null,
    @SerializedName("responseData")
    var responseData: GameDetails? = null,
    @SerializedName("status")
    var status: Int? = null
)
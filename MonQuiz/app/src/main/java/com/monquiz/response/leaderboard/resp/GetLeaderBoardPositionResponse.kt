package com.monquiz.response.leaderboard.resp

import com.google.gson.annotations.SerializedName

data class GetLeaderBoardPositionResponse(
    @SerializedName("status")
    var status: Int? = null,
    @SerializedName("message")
    var message: String? = null,
    @SerializedName("responseData")
    var responseData: String? = null
)
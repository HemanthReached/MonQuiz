package com.monquiz.response.leaderboard.resp


import com.google.gson.annotations.SerializedName

data class LeaderBoard_Resp(
    @SerializedName("message")
    var message: String? = null,
    @SerializedName("status")
    var status: Int? = null
)
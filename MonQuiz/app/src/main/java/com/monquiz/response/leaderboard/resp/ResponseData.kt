package com.monquiz.response.leaderboard.resp

import com.google.gson.annotations.SerializedName

data class ResponseData(
    @SerializedName("leaderBoard")
    var leaderBoard: Int? = null,
    @SerializedName("userName")
    var userName: String? = null
)

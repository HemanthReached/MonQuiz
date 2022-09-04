package com.monquiz.response.leaderboard

import com.google.gson.annotations.SerializedName

data class FinalScore_Input(
    @SerializedName("userId")
    var userId: String? = null,
    @SerializedName("playRoomId")
    var playRoomId: String? = null)

package com.monquiz.response.leaderboard.input


import com.google.gson.annotations.SerializedName

data class LeaderBoard_InputData(
    @SerializedName("looserId")
    var looserId: String? = null,
    @SerializedName("looserScore")
    var looserScore: Int? = null,
    @SerializedName("playRoomId")
    var playRoomId: String? = null,
    @SerializedName("winnerId")
    var winnerId: String? = null,
    @SerializedName("winnerScore")
    var winnerScore: Int? = null,
    @SerializedName("stakeAmount")
    var stakeAmount:Int?=null
)
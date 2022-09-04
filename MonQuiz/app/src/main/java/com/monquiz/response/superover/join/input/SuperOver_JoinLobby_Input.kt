package com.monquiz.response.superover.join.input


import com.google.gson.annotations.SerializedName

data class SuperOver_JoinLobby_Input(
    @SerializedName("stakeAmount")
    var stakeAmount: Int? = null,
    @SerializedName("gameId")
    var gameId: String? = null,
    @SerializedName("stakeId")
    var stakeId: String? = null,
    @SerializedName("percentage")
    var percentage: Int? = null,
    @SerializedName("userId")
    var userId: String? = null,
    @SerializedName("bot")
    var bot: Boolean = false
)
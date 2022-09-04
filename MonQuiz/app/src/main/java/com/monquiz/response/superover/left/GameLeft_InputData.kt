package com.monquiz.response.superover.left


import com.google.gson.annotations.SerializedName

data class GameLeft_InputData(
    @SerializedName("playRoomId")
    var playRoomId: String? = null,
    @SerializedName("userId")
    var userId: String? = null
)
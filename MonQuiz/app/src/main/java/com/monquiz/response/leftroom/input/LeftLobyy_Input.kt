package com.monquiz.response.leftroom.input


import com.google.gson.annotations.SerializedName

data class LeftLobyy_Input(
    @SerializedName("playRoomId")
    var playRoomId: String? = null,
    @SerializedName("userId")
    var userId: String? = null
)
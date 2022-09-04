package com.monquiz.response.superover.exit


import com.google.gson.annotations.SerializedName

data class Game_Lobby_Exit_Input(
    @SerializedName("userId")
    var userId: String? = null
)
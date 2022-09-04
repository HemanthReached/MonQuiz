package com.monquiz.response.superover.qtns.input


import com.google.gson.annotations.SerializedName

data class SuperOver_Questions(
    @SerializedName("playRoomId")
    var playRoomId: String? = null
)
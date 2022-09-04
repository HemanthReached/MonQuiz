package com.monquiz.response.superover.qtns.submit


import com.google.gson.annotations.SerializedName

data class SuperOverQtn_Submit(
    @SerializedName("answerStatus")
    var answerStatus: Boolean? = null,
    @SerializedName("playRoomId")
    var playRoomId: String? = null,
    @SerializedName("userId")
    var userId: String? = null,
    @SerializedName("score")
    var score: Int? = null
)
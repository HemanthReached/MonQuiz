package com.monquiz.response.superover.qtns.submit.resp


import com.google.gson.annotations.SerializedName

data class Player(
    @SerializedName("_id")
    var id: String? = null,
    @SerializedName("photo")
    var photo: String? = null,
    @SerializedName("score")
    var score: Int? = null,
    @SerializedName("userId")
    var userId: String? = null,
    @SerializedName("username")
    var username: String? = null
)
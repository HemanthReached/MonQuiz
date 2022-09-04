package com.monquiz.response.superover.join


import com.google.gson.annotations.SerializedName

data class Score(
    @SerializedName("round")
    var round: Int? = null
)
package com.monquiz.response.superover.qtns.submit.resp


import com.google.gson.annotations.SerializedName

data class Score(
    @SerializedName("round")
    var round: Int? = null
)
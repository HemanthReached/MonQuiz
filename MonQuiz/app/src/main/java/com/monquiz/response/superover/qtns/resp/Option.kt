package com.monquiz.response.superover.qtns.resp


import com.google.gson.annotations.SerializedName

data class Option(
    @SerializedName("text")
    var text: String? = null,
    @SerializedName("val")
    var valX: String? = null
)
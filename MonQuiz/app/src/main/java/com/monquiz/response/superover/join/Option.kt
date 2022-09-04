package com.monquiz.response.superover.join


import com.google.gson.annotations.SerializedName

data class Option(
    @SerializedName("text")
    var text: String? = null,
    @SerializedName("val")
    var valX: String? = null
)
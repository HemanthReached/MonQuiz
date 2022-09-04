package com.monquiz.response.getGamesResponse


import com.google.gson.annotations.SerializedName

data class Get_GamesResponse(
    @SerializedName("message")
    var message: String? = null,
    @SerializedName("responseData")
    var responseData: List<ResponseData>? = null,
    @SerializedName("status")
    var status: Int? = null
)
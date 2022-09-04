package com.monquiz.response.leaderboard.resp

import com.google.gson.JsonObject
import com.google.gson.annotations.SerializedName

data class CheckSum_Resp(
    @SerializedName("message")
    var message: String? = null,
    @SerializedName("status")
    var status: Int? = null,
    @SerializedName("responseData")
    var responseData: JsonObject? = null)

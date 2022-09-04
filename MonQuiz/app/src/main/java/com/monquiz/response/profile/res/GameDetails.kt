package com.monquiz.response.profile.res

import com.google.gson.annotations.SerializedName

data class GameDetails(
    @SerializedName("gamesCount")
    var gamesCount: String? = null,
    @SerializedName("winCount")
    var winCount: String? = null,
    @SerializedName("profit")
    var profit: String? = null,
    @SerializedName("weeklyprofit")
    var weeklyprofit: String? = null)

package com.monquiz.response.getGamesResponse


import com.google.gson.annotations.SerializedName

data class ResponseData(
    @SerializedName("createdAt")
    var createdAt: String? = null,
    @SerializedName("createdBy")
    var createdBy: String? = null,
    @SerializedName("description")
    var description: String? = null,
    @SerializedName("gameId")
    var gameId: String? = null,
    @SerializedName("icon")
    var icon: String? = null,
    @SerializedName("_id")
    var id: String? = null,
    @SerializedName("isLive")
    var isLive: Boolean? = null,
    @SerializedName("name")
    var name: String? = null,
    @SerializedName("status")
    var status: Boolean? = null,
    @SerializedName("updatedAt")
    var updatedAt: String? = null,
    @SerializedName("updatedBy")
    var updatedBy: String? = null
)
package com.monquiz.response.getstakesresp


import com.google.gson.annotations.SerializedName

data class ResponseData(
    @SerializedName("amount")
    var amount: String? = null,
    @SerializedName("createdAt")
    var createdAt: String? = null,
    @SerializedName("createdBy")
    var createdBy: String? = null,
    @SerializedName("description")
    var description: String? = null,
    @SerializedName("icon")
    var icon: String? = null,
    @SerializedName("_id")
    var id: String? = null,
    @SerializedName("isLive")
    var isLive: Boolean? = null,
    @SerializedName("stakeId")
    var stakeId: String? = null,
    @SerializedName("status")
    var status: Boolean? = null,
    @SerializedName("updatedAt")
    var updatedAt: String? = null,
    @SerializedName("updatedBy")
    var updatedBy: String? = null
)
package com.monquiz.response.updatecheck

import com.google.gson.annotations.SerializedName
data class ResponseData(
    @SerializedName("features")
    var features: String? = null,
    @SerializedName("_id")
    var id: String? = null,
    @SerializedName("version")
    var version: Int? = null,
    @SerializedName("createdBy")
    var createdBy: String? = null,
    @SerializedName("updatedBy")
    var updatedBy: String? = null,
    @SerializedName("createdAt")
    var createdAt: String? = null,
    @SerializedName("updatedAt")
    var updatedAt: String? = null,
    @SerializedName("status")
    var status: Boolean? = null,
    @SerializedName("__v")
    var v: String? = null
)

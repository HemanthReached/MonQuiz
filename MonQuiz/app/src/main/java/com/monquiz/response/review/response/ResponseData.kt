package com.monquiz.response.review.response

import com.google.gson.annotations.SerializedName
data class ResponseData(
    @SerializedName("comment")
    var comment: String? = null,
    @SerializedName("_id")
    var id: String? = null,
    @SerializedName("createdAt")
    var createdAt: String? = null,
    @SerializedName("createdBy")
    var createdBy: String? = null,
    @SerializedName("updatedBy")
    var updatedBy: String? = null,
    @SerializedName("updatedAt")
    var updatedAt: String? = null,
)
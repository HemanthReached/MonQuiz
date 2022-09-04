package com.monquiz.response.review.input

import com.google.gson.annotations.SerializedName

data class CreateFeedBackInput(
    @SerializedName("userId")
    var userId: String? = null,
    @SerializedName("reviewId")
    var reviewId: String? = null,
    @SerializedName("comment")
    var comment: String? = null)

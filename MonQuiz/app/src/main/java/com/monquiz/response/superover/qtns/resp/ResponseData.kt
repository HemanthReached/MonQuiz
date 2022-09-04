package com.monquiz.response.superover.qtns.resp


import com.google.gson.annotations.SerializedName

data class ResponseData(
    @SerializedName("answer")
    var answer: String? = null,
    @SerializedName("createdAt")
    var createdAt: String? = null,
    @SerializedName("createdBy")
    var createdBy: String? = null,
    @SerializedName("_id")
    var id: String? = null,
    @SerializedName("question")
    var question:String?=null,
    @SerializedName("options")
    var options: List<Option>? = null,
    @SerializedName("questionId")
    var questionId: String? = null,
    @SerializedName("questionTypeData")
    var questionTypeData: QuestionTypeData? = null,
    @SerializedName("status")
    var status: Boolean? = null,
    @SerializedName("updatedAt")
    var updatedAt: String? = null,
    @SerializedName("updatedBy")
    var updatedBy: String? = null
)
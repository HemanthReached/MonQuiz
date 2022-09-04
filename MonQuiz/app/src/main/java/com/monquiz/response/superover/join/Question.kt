package com.monquiz.response.superover.join


import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.google.gson.annotations.SerializedName
import com.monquiz.db.daos.typeconverter.Deliverytypeconverter

const val superoverqtns = 0

@Entity(tableName = "SuperOver_qtns")
data class Question(
    @SerializedName("answer")
    var answer: String? = null,
    @SerializedName("createdAt")
    var createdAt: String? = null,
    @SerializedName("createdBy")
    var createdBy: String? = null,
    @SerializedName("_id")
    var id: String? = null,
    @SerializedName("options")
    @TypeConverters(Deliverytypeconverter::class)

    var options: List<Option>? = null,
    @SerializedName("question")
    var question: String? = null,
    @SerializedName("status")
    var status: Boolean? = null,
    @SerializedName("updatedAt")
    var updatedAt: String? = null,
    @SerializedName("updatedBy")
    var updatedBy: String? = null,
    @SerializedName("__v")
    var v: Int? = null
)
{
    @PrimaryKey(autoGenerate = false)
    var idss: Int = superoverqtns
}
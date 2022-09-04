package com.monquiz.response.superover.join


import com.google.gson.annotations.SerializedName

data class ResponseData(
    @SerializedName("createdAt")
    var createdAt: String? = null,
    @SerializedName("currentRound")
    var currentRound: Int? = null,
    @SerializedName("gameId")
    var gameId: String? = null,
    @SerializedName("_id")
    var id: String? = null,
    @SerializedName("isGameEnd")
    var isGameEnd: Boolean? = null,
    @SerializedName("isOpen")
    var isOpen: Boolean? = null,
    @SerializedName("noOfPlayers")
    var noOfPlayers: Int? = null,
    @SerializedName("noOfQuestions")
    var noOfQuestions: Int? = null,
    @SerializedName("owner")
    var owner: String? = null,
    @SerializedName("players")
    var players: List<Player>? = null,
    @SerializedName("questions")
    var questions: List<Question>? = null,
    @SerializedName("rounds")
    var rounds: Int? = null,
    @SerializedName("score")
    var score: List<Score>? = null,
    @SerializedName("stakeId")
    var stakeId: String? = null,
    @SerializedName("__v")
    var v: Int? = null
)
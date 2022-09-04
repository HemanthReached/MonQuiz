package com.monquiz.model

class FaqModel() {
    var Heading: String = ""
    var Question: String = ""
    var Answer: String = ""
    var isHeading : Boolean = false


    constructor(Heading: String, Question: String,Answer : String ,isHeading : Boolean) : this() {
        this.Heading = Heading
        this.Question = Question
        this.Answer = Answer
        this.isHeading = isHeading
    }
}
package com.monquiz.response.superover.qtns

data class Question(var question: String?,
                    var options: Map<String, String>?,
                    var answer: String?,
                   var  level: String?,
                    var sub_category: String?,
                    var url: String?,
                  var  question_type: String?,
                   var questionTime: Long) {

}
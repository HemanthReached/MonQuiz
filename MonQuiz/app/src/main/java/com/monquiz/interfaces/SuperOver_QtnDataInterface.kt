package com.monquiz.interfaces

import com.monquiz.response.superover.join.Question
import com.monquiz.response.superover.join.ResponseData

interface SuperOver_QtnDataInterface {
    fun qtnsdata( qtnsdata: ResponseData,  qtnslist:List<Question>)
}